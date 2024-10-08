import pandas as pd
from sklearn.preprocessing import LabelEncoder
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import classification_report
from sklearn.cluster import KMeans
from sklearn.metrics import silhouette_score, davies_bouldin_score, calinski_harabasz_score
import plotly.express as px
import matplotlib.pyplot as plt

# Volvemos a cargar los datos
data = pd.read_csv('grafana_data.csv')

# Verificamos el tipo de la columna 'dt_request_branch_tz'
#print("Tipo de datos de 'dt_request_branch_tz':", pd.api.types.infer_dtype(data['dt_request_branch_tz']))

data['dt_request_branch_tz'] = pd.to_datetime(data['dt_request_branch_tz'], errors='coerce')

# Función 1: Usuarios con más de 3 tarjetas históricamente
def usuarios_mas_3_tarjetas(data):
    tarjetas_por_usuario = data.groupby('customer_name')['masked_pan'].nunique()
    return tarjetas_por_usuario[tarjetas_por_usuario > 3]

# Función 2: Usuarios con más de 2 IPs por día
def usuarios_mas_2_ips_dia(data):
    data['day'] = data['dt_request_branch_tz'].dt.date
    ips_por_dia_usuario = data.groupby(['customer_name', 'day'])['source_ip'].nunique()
    return ips_por_dia_usuario[ips_por_dia_usuario > 2]

# Función 3: Tarjetas con más de 2 usuarios
def tarjetas_mas_2_usuarios(data):
    usuarios_por_tarjeta = data.groupby('masked_pan')['customer_name'].nunique()
    return usuarios_por_tarjeta[usuarios_por_tarjeta > 2]

# Función 4: Más de 1.200.000 en la suma de transacciones por usuario en un día
def suma_mas_1200000_dia(data):
    suma_por_dia_usuario = data.groupby(['customer_name', 'day'])['total_amount'].sum()
    return suma_por_dia_usuario[suma_por_dia_usuario > 1200000]

# Función 5: Más de 5 transacciones por usuario en un día
def mas_5_trxs_por_usuario_dia(data):
    trxs_por_dia_usuario = data.groupby(['customer_name', 'day'])['trx_id'].count()
    return trxs_por_dia_usuario[trxs_por_dia_usuario > 5]

# Función 6: Más de un usuario con más de 4 transacciones del mismo valor
def usuarios_mas_4_trxs_mismo_valor(data):
    data['day'] = data['dt_request_branch_tz'].dt.date
    trxs_por_usuario_valor_dia = data.groupby(['customer_name', 'total_amount', 'day']).size()
    return trxs_por_usuario_valor_dia[trxs_por_usuario_valor_dia > 4]


# Función 7: Usuarios con más de 20 transacciones aprobadas en un mes
def mas_20_trxs_aprobadas_mes(data):
    data['month'] = data['dt_request_branch_tz'].dt.to_period('M')
    trxs_aprobadas = data[data['trx_status_id'] == 'APPROVED']  # Suponiendo que 'APPROVED' indica aprobación
    trxs_aprobadas_por_mes_usuario = trxs_aprobadas.groupby(['customer_name', 'month'])['trx_id'].count()
    return trxs_aprobadas_por_mes_usuario[trxs_aprobadas_por_mes_usuario > 20]

# Función 8: Usuarios con más de 7.030.000 en transacciones aprobadas en un mes
def mas_7030000_aprobadas_mes(data):
    trxs_aprobadas = data[data['trx_status_id'] == 'APPROVED']
    suma_aprobadas_por_mes_usuario = trxs_aprobadas.groupby(['customer_name', 'month'])['total_amount'].sum()
    return suma_aprobadas_por_mes_usuario[suma_aprobadas_por_mes_usuario > 7030000]

#Función para etqieutear los usuarios 0 no riesgo - 1 riesgo
def etiquetar_usuarios_riesgo(data):
    
    data['riesgo'] = 0
    usuarios_3_tarjetas = usuarios_mas_3_tarjetas(data).index
    usuarios_2_ips_dia = usuarios_mas_2_ips_dia(data).index.get_level_values(0)
    tarjetas_2_usuarios = tarjetas_mas_2_usuarios(data).index
    usuarios_1200000_dia = suma_mas_1200000_dia(data).index.get_level_values(0)
    usuarios_5_trxs_dia = mas_5_trxs_por_usuario_dia(data).index.get_level_values(0)
    usuarios_4_trxs_valor = usuarios_mas_4_trxs_mismo_valor(data).index.get_level_values(0)
    usuarios_20_trxs_mes = mas_20_trxs_aprobadas_mes(data).index.get_level_values(0)
    usuarios_7030000_mes = mas_7030000_aprobadas_mes(data).index.get_level_values(0)

    data.loc[data['customer_name'].isin(usuarios_3_tarjetas), 'riesgo'] = 1
    data.loc[data['customer_name'].isin(usuarios_2_ips_dia), 'riesgo'] = 1
    data.loc[data['masked_pan'].isin(tarjetas_2_usuarios), 'riesgo'] = 1
    data.loc[data['customer_name'].isin(usuarios_1200000_dia), 'riesgo'] = 1
    data.loc[data['customer_name'].isin(usuarios_5_trxs_dia), 'riesgo'] = 1
    data.loc[data['customer_name'].isin(usuarios_4_trxs_valor), 'riesgo'] = 1
    data.loc[data['customer_name'].isin(usuarios_20_trxs_mes), 'riesgo'] = 1
    data.loc[data['customer_name'].isin(usuarios_7030000_mes), 'riesgo'] = 1

    return data

data = etiquetar_usuarios_riesgo(data)
data.info()

le = LabelEncoder()

data['merchant_name_encoded'] = le.fit_transform(data['merchant_name'].astype(str))
data['branch_name_encoded'] = le.fit_transform(data['branch_name'].astype(str))
data['trx_status_description_encoded'] = le.fit_transform(data['trx_status_description'].astype(str))
data['masked_pan_encoded'] = le.fit_transform(data['masked_pan'].astype(str))
data['source_ip_encoded'] = le.fit_transform(data['source_ip'].astype(str))

# Selección de características para el modelo
features = [
    'merchant_name_encoded', 'branch_name_encoded', 'trx_status_description_encoded',
    'total_amount', 'masked_pan_encoded', 'source_ip_encoded'
]

X = data[features]
y = data['riesgo']

# División en conjunto de entrenamiento y prueba
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

# Entrenar el modelo de clasificación Random Forest
clf = RandomForestClassifier(random_state=42)
clf.fit(X_train, y_train)
y_pred = clf.predict(X_test)
print(classification_report(y_test, y_pred))


# Agregar Clustering con KMeans
kmeans = KMeans(n_clusters=3, random_state=42)
clusters = kmeans.fit_predict(X)

data['cluster'] = clusters

# Inertia (Inercia)
inertia = kmeans.inertia_
print(f'Inertia: {inertia}')

# Silhouette Score
silhouette_avg = silhouette_score(X, clusters)
print(f'Silhouette Score: {silhouette_avg}')

# Davies-Bouldin Index
davies_bouldin = davies_bouldin_score(X, clusters)
print(f'Davies-Bouldin Index: {davies_bouldin}')

# Calinski-Harabasz Index
calinski_harabasz = calinski_harabasz_score(X, clusters)
print(f'Calinski-Harabasz Index: {calinski_harabasz}')

"""
y_proba = clf.predict_proba(X_test)[:, 1]  


resultados = pd.DataFrame({
    'customer_name': data.loc[X_test.index, 'customer_name'],  
    'probabilidad_riesgo': y_proba,                            
    'riesgo_predicho': y_pred                                  
})

# Mostrar los 10 usuarios con mayor probabilidad de ser de riesgo
usuarios_riesgo = resultados.sort_values(by='probabilidad_riesgo', ascending=False).head(10)
#print(usuarios_riesgo)


# Visualización de las probabilidades de los 10 principales usuarios de riesgo
plt.figure(figsize=(10, 6))
plt.barh(usuarios_riesgo['customer_name'], usuarios_riesgo['probabilidad_riesgo'], color='salmon')
plt.xlabel('Probabilidad de Riesgo')
plt.title('Usuarios con Mayor Probabilidad de Riesgo')
plt.gca().invert_yaxis()  
plt.show()


# Ejecutamos las funciones y mostramos los resultados
print("Usuarios con más de 3 tarjetas:", usuarios_mas_3_tarjetas(data))
print("Usuarios con más de 2 IPs por día:", usuarios_mas_2_ips_dia(data))
print("Tarjetas con más de 2 usuarios:", tarjetas_mas_2_usuarios(data))
print("Más de 1.200.000 en la suma de trxs por usuario en un día:", suma_mas_1200000_dia(data))
print("Más de 5 trxs por usuario en un día:", mas_5_trxs_por_usuario_dia(data))
print("Usuarios con más de 4 trxs del mismo valor:", usuarios_mas_4_trxs_mismo_valor(data))
print("Usuarios con más de 20 trxs aprobadas en el mes:", mas_20_trxs_aprobadas_mes(data))
print("Usuarios con más de 7.030.000 trxs aprobadas en el mes:", mas_7030000_aprobadas_mes(data))
"""