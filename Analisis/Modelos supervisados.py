import pandas as pd
from sklearn.preprocessing import LabelEncoder
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import classification_report
from sklearn.ensemble import GradientBoostingClassifier
from sklearn.metrics import confusion_matrix
from collections import defaultdict

# cloustering y grafica
from sklearn.metrics import silhouette_score, davies_bouldin_score, calinski_harabasz_score
from sklearn.cluster import DBSCAN
from sklearn.preprocessing import StandardScaler
import seaborn as sns
import matplotlib.pyplot as plt

from sklearn.cluster import KMeans
from sklearn.preprocessing import MinMaxScaler
from sklearn.decomposition import PCA
import plotly.express as px
import plotly.graph_objects as go

# Volvemos a cargar los datos
data = pd.read_csv('grafana_data.csv')

# Verificamos el tipo de la columna 'dt_request_branch_tz'
#print("Tipo de datos de 'dt_request_branch_tz':", pd.api.types.infer_dtype(data['dt_request_branch_tz']))

# Convertimos las fechas a formato datetime
data = pd.read_csv('grafana_data.csv')
data['dt_request_branch_tz'] = pd.to_datetime(data['dt_request_branch_tz'], errors='coerce')

# Función 1: Usuarios con más de 3 tarjetas históricamente
def usuarios_mas_3_tarjetas(data):
    tarjeta_count = defaultdict(set)  # Usar set para evitar duplicados
    for idx, row in data.iterrows():
        tarjeta_count[row['customer_name']].add(row['masked_pan'])  # Añadir tarjeta al conjunto
    return [user for user, tarjetas in tarjeta_count.items() if len(tarjetas) > 3]

# Función 2: Usuarios con más de 2 IPs por día
def usuarios_mas_2_ips_dia(data):
    ip_count = defaultdict(lambda: defaultdict(set))  # Diccionario anidado para contar por día
    for idx, row in data.iterrows():
        day = row['dt_request_branch_tz'].date()
        ip_count[row['customer_name']][day].add(row['source_ip'])
    
    # Filtrar usuarios con más de 2 IPs por día
    usuarios_riesgo = []
    for user, days in ip_count.items():
        for day, ips in days.items():
            if len(ips) > 2:
                usuarios_riesgo.append((user, day))  # Guardar usuario y el día de riesgo
    return usuarios_riesgo

# Función 3: Tarjetas con más de 2 usuarios
def tarjetas_mas_2_usuarios(data):
    tarjeta_usuarios = defaultdict(set)
    for idx, row in data.iterrows():
        tarjeta_usuarios[row['masked_pan']].add(row['customer_name'])
    
    # Filtrar tarjetas que han sido usadas por más de 2 usuarios
    return [tarjeta for tarjeta, usuarios in tarjeta_usuarios.items() if len(usuarios) > 2]

# Función 4: Más de 1.200.000 en la suma de transacciones por usuario en un día
def suma_mas_1200000_dia(data):
    suma_por_dia = defaultdict(lambda: defaultdict(float))  # Diccionario anidado para sumar por día
    for idx, row in data.iterrows():
        day = row['dt_request_branch_tz'].date()
        suma_por_dia[row['customer_name']][day] += row['total_amount']
    
    # Filtrar usuarios con suma mayor a 1.200.000 en un día
    usuarios_riesgo = []
    for user, days in suma_por_dia.items():
        for day, suma in days.items():
            if suma > 1200000:
                usuarios_riesgo.append((user, day))  # Guardar usuario y día de riesgo
    return usuarios_riesgo

# Función 5: Más de 5 transacciones por usuario en un día
def mas_5_trxs_por_usuario_dia(data):
    trx_count = defaultdict(lambda: defaultdict(int))  # Diccionario anidado para contar transacciones por día
    for idx, row in data.iterrows():
        day = row['dt_request_branch_tz'].date()
        trx_count[row['customer_name']][day] += 1
    
    # Filtrar usuarios con más de 5 transacciones en un día
    usuarios_riesgo = []
    for user, days in trx_count.items():
        for day, count in days.items():
            if count > 5:
                usuarios_riesgo.append((user, day))
    return usuarios_riesgo

# Función 6: Más de un usuario con más de 4 transacciones del mismo valor en el mismo día
def usuarios_mas_4_trxs_mismo_valor(data):
    valor_count = defaultdict(lambda: defaultdict(lambda: defaultdict(int)))  # Diccionario anidado para contar valor por día
    for idx, row in data.iterrows():
        day = row['dt_request_branch_tz'].date()
        valor_count[row['customer_name']][day][row['total_amount']] += 1
    
    # Filtrar usuarios con más de 4 transacciones del mismo valor en el mismo día
    usuarios_riesgo = []
    for user, days in valor_count.items():
        for day, valores in days.items():
            for valor, count in valores.items():
                if count > 4:
                    usuarios_riesgo.append((user, day, valor))
    return usuarios_riesgo

# Función 7: Usuarios con más de 20 transacciones aprobadas en un mes
def mas_20_trxs_aprobadas_mes(data):
    trx_aprobadas = defaultdict(lambda: defaultdict(int))  # Diccionario anidado para contar transacciones aprobadas por mes
    for idx, row in data.iterrows():
        if row['trx_status_id'] == 'APPROVED':
            mes = row['dt_request_branch_tz'].to_period('M')
            trx_aprobadas[row['customer_name']][mes] += 1
    
    # Filtrar usuarios con más de 20 transacciones aprobadas en un mes
    usuarios_riesgo = []
    for user, meses in trx_aprobadas.items():
        for mes, count in meses.items():
            if count > 20:
                usuarios_riesgo.append((user, mes))
    return usuarios_riesgo

# Función 8: Usuarios con más de 7.030.000 en transacciones aprobadas en un mes
def mas_7030000_aprobadas_mes(data):
    suma_aprobada = defaultdict(lambda: defaultdict(float))  # Diccionario anidado para sumar montos aprobados por mes
    for idx, row in data.iterrows():
        if row['trx_status_id'] == 'APPROVED':
            mes = row['dt_request_branch_tz'].to_period('M')
            suma_aprobada[row['customer_name']][mes] += row['total_amount']
    
    # Filtrar usuarios con más de 7.030.000 en transacciones aprobadas en un mes
    usuarios_riesgo = []
    for user, meses in suma_aprobada.items():
        for mes, total in meses.items():
            if total > 7030000:
                usuarios_riesgo.append((user, mes))
    return usuarios_riesgo

# Función para etiquetar los usuarios 0 (no riesgo) - 1 (riesgo)
def etiquetar_usuarios_riesgo(data):
    # Inicializamos una nueva columna para 'riesgo'
    data['riesgo'] = 0

    # Ejecutamos las funciones que devuelven los usuarios en riesgo según cada criterio
    usuarios_3_tarjetas = usuarios_mas_3_tarjetas(data)  # Lista de usuarios con más de 3 tarjetas
    usuarios_2_ips_dia = [x[0] for x in usuarios_mas_2_ips_dia(data)]  # Solo usuarios (sin días)
    tarjetas_2_usuarios = tarjetas_mas_2_usuarios(data)  # Lista de tarjetas con más de 2 usuarios
    usuarios_1200000_dia = [x[0] for x in suma_mas_1200000_dia(data)]  # Solo usuarios (sin días)
    usuarios_5_trxs_dia = [x[0] for x in mas_5_trxs_por_usuario_dia(data)]  # Solo usuarios (sin días)
    usuarios_4_trxs_valor = [x[0] for x in usuarios_mas_4_trxs_mismo_valor(data)]  # Solo usuarios (sin días)
    usuarios_20_trxs_mes = [x[0] for x in mas_20_trxs_aprobadas_mes(data)]  # Solo usuarios (sin meses)
    usuarios_7030000_mes = [x[0] for x in mas_7030000_aprobadas_mes(data)]  # Solo usuarios (sin meses)

    # Etiquetar los usuarios en la columna 'riesgo' si cumplen con alguno de los criterios
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
data['billing_document_encode'] = le.fit_transform(data['billing_document'].astype(str))
data['customer_name_encode'] = le.fit_transform(data['customer_name'].astype(str))
data['router_id'] = le.fit_transform(data['router_id'].astype(str))
data['customer_email'] = le.fit_transform(data['customer_email'].astype(str))

# Selección de características para el modelo
features = [
    'merchant_name_encoded', 'branch_name_encoded', 'trx_status_description_encoded',
    'total_amount', 'masked_pan_encoded', 'source_ip_encoded'
]

X = data[features]
y = data['riesgo']

# División en conjunto de entrenamiento y prueba
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

#caracteristicas del modelo 2
feature2 = ['merchant_name_encoded','merchant_id', 'trx_status_description_encoded', 'total_amount', 'masked_pan_encoded', 'source_ip_encoded',
             'billing_document_encode', 'customer_name_encode','router_id', 'customer_email']

X2 = data[feature2]
y2 = data['riesgo']

# División en conjunto de entrenamiento y prueba
X_train2, X_test2, y_train2, y_test2 = train_test_split(X2, y2, test_size=0.2, random_state=42)

# Entrenar el modelo de clasificación Random Forest
clf = RandomForestClassifier(random_state=42)
clf.fit(X_train, y_train)
y_pred = clf.predict(X_test)
print("Reporte de clasificación para el modelo Random Forest:")
print(classification_report(y_test, y_pred))


# Entrenar el modelo de clasificación Gradient Boosting
clf = GradientBoostingClassifier(n_estimators=100, learning_rate=1.0,max_depth=3).fit(X_train2, y_train2)
clf.score(X_test2, y_test2)
y_pred2 = clf.predict(X_test2)
print("Reporte de clasificación para el modelo XGBoost:")
print(classification_report(y_test2, y_pred2))

# Calcular la matriz de confusión
cm = confusion_matrix(y_test, y_pred)

# Visualizar la matriz de confusión
sns.heatmap(cm, annot=True, fmt='d', cmap='Blues', xticklabels=['Legítimo', 'Fraude'], yticklabels=['Legítimo', 'Fraude'])
plt.xlabel('Clase Predicha')
plt.ylabel('Clase Verdadera')
plt.title('Matriz de Confusión')
plt.show()


#Usuarios con mayor probabilidad de fraude
y_proba = clf.predict_proba(X_test2)[:, 1]  

resultados = pd.DataFrame({
    'customer_name': data.loc[X_test2.index, 'customer_name'],  # Asumiendo que tienes esta columna
    'billing_document': data.loc[X_test2.index, 'billing_document'],
    'probabilidad_fraude': y_proba,
    'fraude_predicho': y_pred
})

usuarios_fraude_riesgo = resultados[resultados['fraude_predicho'] == 1].sort_values(by='probabilidad_fraude', ascending=False)
print(usuarios_fraude_riesgo.head(10))


# Listar las 10 transacciones con mayor probabilidad de fraude
transacciones_fraude_riesgo = resultados.sort_values(by='probabilidad_fraude', ascending=False).head(10)
print(transacciones_fraude_riesgo[['billing_document', 'probabilidad_fraude']])


#porcentaje de fraude detectado
total_fraudes_predichos = sum(y_pred)
total_transacciones = len(y_pred)

porcentaje_fraudes = (total_fraudes_predichos / total_transacciones) * 100
print(f'Porcentaje de fraudes detectados: {porcentaje_fraudes:.2f}%')

#numero de transacciones fraudulentas por usuario
fraudes_por_usuario = resultados[resultados['fraude_predicho'] == 1].groupby('customer_name').size()
print("numero de transacciones fraudulentas por usuario",fraudes_por_usuario)

data['dt_request_branch_tz'] = pd.to_datetime(data['dt_request_branch_tz'])

# Crear la columna 'mes' en el DataFrame original
data['mes'] = data['dt_request_branch_tz'].dt.to_period('M')

# Crear el DataFrame de resultados de predicciones con la columna 'mes'
resultados = pd.DataFrame({
    'customer_name': data.loc[X_test.index, 'customer_name'],  
    'billing_document': data.loc[X_test.index, 'billing_document'],
    'probabilidad_fraude': y_proba,
    'fraude_predicho': y_pred,
    'mes': data.loc[X_test.index, 'mes']  # Añadimos la columna 'mes' aquí
})

# Filtrar solo las transacciones fraudulentas predichas
transacciones_fraude = resultados[resultados['fraude_predicho'] == 1]

# Contar fraudes por mes
fraudes_por_mes = transacciones_fraude.groupby('mes').size()

# Graficar fraudes por mes
fraudes_por_mes.plot(kind='bar')
plt.title('Fraudes Detectados por Mes')
plt.xlabel('Mes')
plt.ylabel('Número de Fraudes')
plt.show()

"""
# Normalizar los datos para que todas las columnas tengan la misma escala usando MinMaxScaler
scaler = MinMaxScaler()
X_scaled = scaler.fit_transform(X2)

# Aplicar el método del codo para determinar el número óptimo de clústeres
inertia = []
for k in range(1, 20):  # Ajusta el rango de clusters si es necesario
    kmeans = KMeans(n_clusters=k, random_state=42)
    kmeans.fit(X_scaled)
    inertia.append(kmeans.inertia_)

# Elegir el número de clusters (ajústalo según la gráfica del codo o por dominio del problema)
num_clusters = 4  # Por ejemplo, elegimos 4 clusters, ajusta según tus necesidades
kmeans = KMeans(n_clusters=num_clusters, random_state=42)
data['cluster'] = kmeans.fit_predict(X_scaled)

# Agrupar los datos por cluster
grouped_data = data.groupby('cluster')

# Reducir la dimensionalidad para visualización (puedes ajustar esto según tus necesidades)
pca = PCA(n_components=2)
X_pca = pca.fit_transform(X_scaled)
data['pca1'] = X_pca[:, 0]
data['pca2'] = X_pca[:, 1]

# Mapeo de nombres de clusters
cluster_names = {i: f'Grupo {i + 1}' for i in range(num_clusters)}
data['cluster_name'] = data['cluster'].map(cluster_names)

# Crear el gráfico de clustering interactivo con Plotly
fig = px.scatter(
    data,
    x='pca1',
    y='pca2',
    color='cluster',
    hover_data=['customer_name', 'billing_document', 'total_amount'],
    title='Clustering de Usuarios'
)

# Dibujar círculos alrededor de los puntos de cada cluster
for cluster_id, cluster_name in cluster_names.items():
    cluster_data = data[data['cluster'] == cluster_id]
    cluster_center = (cluster_data['pca1'].mean(), cluster_data['pca2'].mean())
    max_distance = max(cluster_data.apply(lambda row: ((row['pca1'] - cluster_center[0])**2 + (row['pca2'] - cluster_center[1])**2)**0.5, axis=1))

    # Agregar un círculo con el mismo color que el cluster
    fig.add_shape(
        type='circle',
        x0=cluster_center[0] - max_distance,
        y0=cluster_center[1] - max_distance,
        x1=cluster_center[0] + max_distance,
        y1=cluster_center[1] + max_distance,
        line=dict(color='black', dash='dash'),
        opacity=0.5,
    )

    # Etiquetar el centroide del cluster con el nombre del cluster
    fig.add_annotation(
        x=cluster_center[0],
        y=cluster_center[1] + 0.1,
        text=cluster_name,
        showarrow=False,
        font=dict(color=px.colors.qualitative.Set1[cluster_id % len(px.colors.qualitative.Set1)], size=12),
    )

# Personalizar ejes y diseño
fig.update_layout(
    xaxis_title='Componente Principal 1',
    yaxis_title='Componente Principal 2',
    title='Clustering de Usuarios con KMeans'
)

# Convertir el gráfico de Plotly a una cadena HTML si es necesario
grafico_html = fig.to_html()

# Mostrar el gráfico en el notebook o el entorno de ejecución
fig.show()



# Normalizar los datos usando StandardScaler (DBSCAN necesita datos escalados)
scaler = StandardScaler()
X_scaled = scaler.fit_transform(X2)

# Aplicar DBSCAN para clustering
dbscan = DBSCAN(eps=0.8, min_samples=10)  # Ajusta los parámetros eps y min_samples según tus necesidades
clusters_dbscan = dbscan.fit_predict(X_scaled)

# Asignar los clusters de DBSCAN al dataset
data['dbscan_cluster'] = clusters_dbscan

# Reducir la dimensionalidad para visualización usando PCA (2 componentes principales)
pca = PCA(n_components=2)
pca_result_dbscan = pca.fit_transform(X_scaled)

# Añadir los componentes principales al dataframe para visualización
data['pca1_dbscan'] = pca_result_dbscan[:, 0]
data['pca2_dbscan'] = pca_result_dbscan[:, 1]

# Crear el gráfico de clustering interactivo con Plotly
fig = px.scatter(
    data,
    x='pca1_dbscan',
    y='pca2_dbscan',
    color='dbscan_cluster',
    hover_data=['customer_name', 'billing_document', 'total_amount'], 
    title='Clustering de Usuarios con DBSCAN'
)

# Dibujar círculos alrededor de los puntos de cada cluster (mismo código de antes)
for cluster_id in sorted(data['dbscan_cluster'].unique()):
    if cluster_id != -1:  # Ignorar el ruido (cluster -1)
        cluster_data = data[data['dbscan_cluster'] == cluster_id]
        cluster_center = (cluster_data['pca1_dbscan'].mean(), cluster_data['pca2_dbscan'].mean())
        max_distance = max(cluster_data.apply(lambda row: ((row['pca1_dbscan'] - cluster_center[0])**2 + (row['pca2_dbscan'] - cluster_center[1])**2)**0.5, axis=1))

        # Agregar un círculo alrededor del cluster
        fig.add_shape(
            type='circle',
            x0=cluster_center[0] - max_distance,
            y0=cluster_center[1] - max_distance,
            x1=cluster_center[0] + max_distance,
            y1=cluster_center[1] + max_distance,
            line=dict(color='black', dash='dash'),
            opacity=0.5,
        )

        # Etiquetar el centroide del cluster
        fig.add_annotation(
            x=cluster_center[0],
            y=cluster_center[1] + 0.1,
            text=f'Grupo {cluster_id + 1}',
            showarrow=False,
            font=dict(color='black', size=12),
        )

# Mostrar el gráfico interactivo en el notebook o entorno de ejecución
fig.show()

silhouette_dbscan = silhouette_score(X_scaled, clusters_dbscan)
davies_bouldin_dbscan = davies_bouldin_score(X_scaled, clusters_dbscan)

print(f"Silhouette Score (DBSCAN): {silhouette_dbscan}")
print(f"Davies-Bouldin Index (DBSCAN): {davies_bouldin_dbscan}")

# Calcular las métricas de evaluación del clustering
silhouette_avg = silhouette_score(X_scaled, data['cluster'])
davies_bouldin = davies_bouldin_score(X_scaled, data['cluster'])
calinski_harabasz = calinski_harabasz_score(X_scaled, data['cluster'])

# Imprimir las métricas
print(f"Silhouette Score (KMeans): {silhouette_avg}")
print(f"Davies-Bouldin Index (KMeans): {davies_bouldin}")
print(f"Calinski-Harabasz Index (KMeans): {calinski_harabasz}")


"""
"""
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