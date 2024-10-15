import pandas as pd
from sklearn.preprocessing import LabelEncoder
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import classification_report, confusion_matrix
from sklearn.ensemble import GradientBoostingClassifier
from collections import defaultdict
import seaborn as sns
import matplotlib.pyplot as plt
from sklearn.preprocessing import StandardScaler
from sklearn.metrics import silhouette_score, davies_bouldin_score, calinski_harabasz_score
from sklearn.cluster import DBSCAN
from sklearn.cluster import KMeans
from sklearn.decomposition import PCA
import plotly.express as px
import plotly.graph_objects as go

# Cargar los datos
data = pd.read_csv('grafana_data.csv')
data['dt_request_branch_tz'] = pd.to_datetime(data['dt_request_branch_tz'], errors='coerce')

# Función 1: Usuarios con más de 3 tarjetas históricamente
def usuarios_mas_3_tarjetas(data):
    tarjeta_count = defaultdict(set)
    for idx, row in data.iterrows():
        tarjeta_count[row['customer_name']].add(row['masked_pan'])
    return [user for user, tarjetas in tarjeta_count.items() if len(tarjetas) > 3]

# Función 2: Usuarios con más de 2 IPs por día
def usuarios_mas_2_ips_dia(data):
    ip_count = defaultdict(lambda: defaultdict(set))
    for idx, row in data.iterrows():
        day = row['dt_request_branch_tz'].date()
        ip_count[row['customer_name']][day].add(row['source_ip'])
    return [(user, day) for user, days in ip_count.items() for day, ips in days.items() if len(ips) > 2]

# Función 3: Tarjetas con más de 2 usuarios
def tarjetas_mas_2_usuarios(data):
    tarjeta_usuarios = defaultdict(set)
    for idx, row in data.iterrows():
        tarjeta_usuarios[row['masked_pan']].add(row['customer_name'])
    return [tarjeta for tarjeta, usuarios in tarjeta_usuarios.items() if len(usuarios) > 2]

# Función 4: Más de 1.200.000 en la suma de transacciones por usuario en un día
def suma_mas_1200000_dia(data):
    suma_por_dia = defaultdict(lambda: defaultdict(float))
    for idx, row in data.iterrows():
        day = row['dt_request_branch_tz'].date()
        suma_por_dia[row['customer_name']][day] += row['total_amount']
    return [(user, day) for user, days in suma_por_dia.items() for day, suma in days.items() if suma > 1200000]

# Función 5: Más de 5 transacciones por usuario en un día
def mas_5_trxs_por_usuario_dia(data):
    trx_count = defaultdict(lambda: defaultdict(int))
    for idx, row in data.iterrows():
        day = row['dt_request_branch_tz'].date()
        trx_count[row['customer_name']][day] += 1
    return [(user, day) for user, days in trx_count.items() for day, count in days.items() if count > 5]

# Función 6: Más de un usuario con más de 4 transacciones del mismo valor en el mismo día
def usuarios_mas_4_trxs_mismo_valor(data):
    valor_count = defaultdict(lambda: defaultdict(lambda: defaultdict(int)))
    for idx, row in data.iterrows():
        day = row['dt_request_branch_tz'].date()
        valor_count[row['customer_name']][day][row['total_amount']] += 1
    return [(user, day, valor) for user, days in valor_count.items() for day, valores in days.items() for valor, count in valores.items() if count > 4]

# Función 7: Usuarios con más de 20 transacciones aprobadas en un mes
def mas_20_trxs_aprobadas_mes(data):
    trx_aprobadas = defaultdict(lambda: defaultdict(int))
    for idx, row in data.iterrows():
        if row['trx_status_id'] == 'APPROVED':
            mes = row['dt_request_branch_tz'].to_period('M')
            trx_aprobadas[row['customer_name']][mes] += 1
    return [(user, mes) for user, meses in trx_aprobadas.items() for mes, count in meses.items() if count > 20]

# Función 8: Usuarios con más de 7.030.000 en transacciones aprobadas en un mes
def mas_7030000_aprobadas_mes(data):
    suma_aprobada = defaultdict(lambda: defaultdict(float))
    for idx, row in data.iterrows():
        if row['trx_status_id'] == 'APPROVED':
            mes = row['dt_request_branch_tz'].to_period('M')
            suma_aprobada[row['customer_name']][mes] += row['total_amount']
    return [(user, mes) for user, meses in suma_aprobada.items() for mes, total in meses.items() if total > 7030000]

# Función para etiquetar los usuarios con valores ponderados
def etiquetar_usuarios_riesgo(data):
    data['riesgo'] = 0
    usuarios_3_tarjetas = usuarios_mas_3_tarjetas(data)
    usuarios_2_ips_dia = [x[0] for x in usuarios_mas_2_ips_dia(data)]
    tarjetas_2_usuarios = tarjetas_mas_2_usuarios(data)
    usuarios_1200000_dia = [x[0] for x in suma_mas_1200000_dia(data)]
    usuarios_5_trxs_dia = [x[0] for x in mas_5_trxs_por_usuario_dia(data)]
    usuarios_4_trxs_valor = [x[0] for x in usuarios_mas_4_trxs_mismo_valor(data)]
    usuarios_20_trxs_mes = [x[0] for x in mas_20_trxs_aprobadas_mes(data)]
    usuarios_7030000_mes = [x[0] for x in mas_7030000_aprobadas_mes(data)]

    # Aplicamos un valor ponderado de 0.25 en lugar de 1
    data.loc[data['customer_name'].isin(usuarios_3_tarjetas), 'riesgo'] += 0.25
    data.loc[data['customer_name'].isin(usuarios_2_ips_dia), 'riesgo'] += 0.25
    data.loc[data['masked_pan'].isin(tarjetas_2_usuarios), 'riesgo'] += 0.25
    data.loc[data['customer_name'].isin(usuarios_1200000_dia), 'riesgo'] += 0.25
    data.loc[data['customer_name'].isin(usuarios_5_trxs_dia), 'riesgo'] += 0.25
    data.loc[data['customer_name'].isin(usuarios_4_trxs_valor), 'riesgo'] += 0.25
    data.loc[data['customer_name'].isin(usuarios_20_trxs_mes), 'riesgo'] += 0.25
    data.loc[data['customer_name'].isin(usuarios_7030000_mes), 'riesgo'] += 0.25

    return data
# Etiquetar usuarios con la función de riesgo
data = etiquetar_usuarios_riesgo(data)

# Discretizar los valores de riesgo en clases categóricas (por ejemplo: 0 = bajo riesgo, 1 = alto riesgo)
# Si el riesgo es mayor que 0.5, consideramos que el usuario es de alto riesgo (1), de lo contrario, bajo riesgo (0)
data['riesgo'] = data['riesgo'].apply(lambda x: 1 if x >= 0.5 else 0)

# Preprocesar las variables categóricas
le = LabelEncoder()
for column in ['merchant_name', 'branch_name', 'trx_status_description', 'masked_pan', 'source_ip', 'billing_document', 'customer_name', 'router_id', 'customer_email']:
    data[f'{column}_encoded'] = le.fit_transform(data[column].astype(str))

# Características para el modelo
features = ['merchant_name_encoded', 'branch_name_encoded', 'trx_status_description_encoded', 'total_amount', 'masked_pan_encoded', 'source_ip_encoded']
X = data[features]
y = data['riesgo']

# División en conjunto de entrenamiento y prueba
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

# Entrenar Random Forest
clf_rf = RandomForestClassifier(random_state=42)
clf_rf.fit(X_train, y_train)
y_pred_rf = clf_rf.predict(X_test)

# Entrenar Gradient Boosting
clf_gb = GradientBoostingClassifier(n_estimators=100, learning_rate=1.0, max_depth=3, random_state=42)
clf_gb.fit(X_train, y_train)
y_pred_gb = clf_gb.predict(X_test)

# Reporte de clasificación
print("Gradient Boosting Classification Report:")
print(classification_report(y_test, y_pred_gb))

# Generar las probabilidades de fraude para cada transacción
y_proba_rf = clf_rf.predict_proba(X_test)[:, 1]  # Probabilidad de fraude

# Reporte de clasificación
print("Random Forest Classification Report:")
print(classification_report(y_test, y_pred_rf))

# Matriz de confusión
cm_rf = confusion_matrix(y_test, y_pred_rf)
sns.heatmap(cm_rf, annot=True, fmt='d', cmap='Blues')
plt.title('Matriz de Confusión - Random Forest')
plt.show()


# Creación del DataFrame con los resultados
resultados = pd.DataFrame({
    'customer_name': data.loc[X_test.index, 'customer_name'],
    'billing_document': data.loc[X_test.index, 'billing_document'],
    'probabilidad_fraude': y_proba_rf,
    'fraude_predicho': y_pred_rf
})

# 1. Usuarios con mayor probabilidad de fraude
usuarios_fraude_riesgo = resultados[resultados['fraude_predicho'] == 1].sort_values(by='probabilidad_fraude', ascending=False)
print("\nUsuarios con mayor probabilidad de fraude:")
print(usuarios_fraude_riesgo.head(10))

# 2. Listar las 10 transacciones con mayor probabilidad de fraude
transacciones_fraude_riesgo = resultados.sort_values(by='probabilidad_fraude', ascending=False).head(10)
print("\nLas 10 transacciones con mayor probabilidad de fraude:")
print(transacciones_fraude_riesgo[['billing_document', 'probabilidad_fraude']])

# 3. Porcentaje de fraude detectado
total_fraudes_predichos = sum(y_pred_rf)
total_transacciones = len(y_pred_rf)
porcentaje_fraudes = (total_fraudes_predichos / total_transacciones) * 100
print(f'\nPorcentaje de fraudes detectados: {porcentaje_fraudes:.2f}%')

# 4. Número de transacciones fraudulentas por usuario
fraudes_por_usuario = resultados[resultados['fraude_predicho'] == 1].groupby('customer_name').size()
print("\nNúmero de transacciones fraudulentas por usuario:")
print(fraudes_por_usuario)

# 5. Fraudes por mes
# Asegúrate de que 'dt_request_branch_tz' esté en formato datetime
data['dt_request_branch_tz'] = pd.to_datetime(data['dt_request_branch_tz'])

# Crear la columna 'mes' en el DataFrame original
data['mes'] = data['dt_request_branch_tz'].dt.to_period('M')

# Crear el DataFrame de resultados de predicciones con la columna 'mes'
resultados = pd.DataFrame({
    'customer_name': data.loc[X_test.index, 'customer_name'],
    'billing_document': data.loc[X_test.index, 'billing_document'],
    'probabilidad_fraude': y_proba_rf,
    'fraude_predicho': y_pred_rf,
    'mes': data.loc[X_test.index, 'mes']
})

# Filtrar solo las transacciones fraudulentas predichas
transacciones_fraude = resultados[resultados['fraude_predicho'] == 1]

# Contar fraudes por mes
fraudes_por_mes = transacciones_fraude.groupby('mes').size()

# Graficar fraudes por mes
plt.figure(figsize=(10, 6))
fraudes_por_mes.plot(kind='bar', color='salmon')
plt.title('Fraudes Detectados por Mes')
plt.xlabel('Mes')
plt.ylabel('Número de Fraudes')
plt.show()