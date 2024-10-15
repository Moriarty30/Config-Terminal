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
data['riesgo'] = data['riesgo'].apply(lambda x: 1 if x >= 0.5 else 0)

# Función para agrupar usuarios por nombre y documento
def agrupar_usuarios_unicos(data):
    data['customer_name'] = data['customer_name'].str.lower().str.strip()
    return data

data = agrupar_usuarios_unicos(data)

# Preprocesar las variables categóricas
le = LabelEncoder()
for column in ['merchant_name', 'branch_name', 'trx_status_description', 'masked_pan', 'source_ip', 'billing_document', 'customer_name', 'router_id', 'customer_email']:
    data[f'{column}_encoded'] = le.fit_transform(data[column].astype(str))

# Características para el modelo
features = ['merchant_name_encoded', 'branch_name_encoded', 'trx_status_description_encoded', 'total_amount', 'masked_pan_encoded', 'source_ip_encoded']
X = data[features]
y = data['riesgo']

feature2 = ['merchant_name_encoded','merchant_id', 'trx_status_description_encoded', 'total_amount', 'masked_pan_encoded', 'source_ip_encoded',
             'billing_document_encoded', 'customer_name_encoded','router_id_encoded', 'customer_email_encoded']

X2 = data[feature2]
y2 = data['riesgo']

# División en conjunto de entrenamiento y prueba para ambos modelos
X_train2, X_test2, y_train2, y_test2 = train_test_split(X2, y2, test_size=0.2, random_state=42)
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

# Entrenar el modelo Random Forest
clf_rf = RandomForestClassifier(random_state=42)
clf_rf.fit(X_train, y_train)

# Entrenar el modelo XGBoost
clf_xgb = GradientBoostingClassifier(n_estimators=100, learning_rate=1.0, max_depth=3, random_state=42)
clf_xgb.fit(X_train2, y_train2)

# Predecir el riesgo de fraude para ambos modelos
y_pred_rf = clf_rf.predict(X_test)
y_proba_rf = clf_rf.predict_proba(X_test)[:, 1]

y_pred_xgb = clf_xgb.predict(X_test2)
y_proba_xgb = clf_xgb.predict_proba(X_test2)[:, 1]

# Creación del DataFrame con los resultados de Random Forest
resultados_rf = pd.DataFrame({
    'customer_name': data.loc[X_test.index, 'customer_name'],
    'billing_document': data.loc[X_test.index, 'billing_document'],
    'masked_pan': data.loc[X_test.index, 'masked_pan'],
    'probabilidad_fraude': y_proba_rf,
    'fraude_predicho': y_pred_rf
})

# Creación del DataFrame con los resultados de XGBoost
resultados_xgb = pd.DataFrame({
    'customer_name': data.loc[X_test2.index, 'customer_name'],
    'billing_document': data.loc[X_test2.index, 'billing_document'],
    'masked_pan': data.loc[X_test2.index, 'masked_pan'],
    'probabilidad_fraude': y_proba_xgb,
    'fraude_predicho': y_pred_xgb
})

# AGRUPAR POR USUARIO: Obtener promedio de probabilidad de fraude por usuario para ambos modelos
fraude_por_usuario_rf = resultados_rf.groupby('customer_name').agg({
    'probabilidad_fraude': 'mean',  # Promedio de probabilidad de fraude
    'fraude_predicho': 'sum'  # Suma de las transacciones predichas como fraude
}).reset_index()

fraude_por_usuario_xgb = resultados_xgb.groupby('customer_name').agg({
    'probabilidad_fraude': 'mean',  # Promedio de probabilidad de fraude
    'fraude_predicho': 'sum'  # Suma de las transacciones predichas como fraude
}).reset_index()

# Definir umbral para clasificar usuarios como de riesgo
umbral_fraude = 0.5  # Ajustar este umbral según análisis
fraude_por_usuario_rf['usuario_riesgo'] = fraude_por_usuario_rf['probabilidad_fraude'] > umbral_fraude
fraude_por_usuario_xgb['usuario_riesgo'] = fraude_por_usuario_xgb['probabilidad_fraude'] > umbral_fraude

# Usuarios con mayor probabilidad de cometer fraude
usuarios_fraude_riesgo_rf = fraude_por_usuario_rf[fraude_por_usuario_rf['usuario_riesgo'] == True].sort_values(by='probabilidad_fraude', ascending=False)
usuarios_fraude_riesgo_xgb = fraude_por_usuario_xgb[fraude_por_usuario_xgb['usuario_riesgo'] == True].sort_values(by='probabilidad_fraude', ascending=False)

# Imprimir resultados de los usuarios de mayor riesgo
print("Usuarios con mayor probabilidad de cometer fraude (Random Forest):")
print(usuarios_fraude_riesgo_rf.head(10))

print("Usuarios con mayor probabilidad de cometer fraude (XGBoost):")
print(usuarios_fraude_riesgo_xgb.head(10))

# Visualizar la distribución de los usuarios según su riesgo de fraude (Random Forest)
plt.figure(figsize=(10, 6))
sns.histplot(fraude_por_usuario_rf['probabilidad_fraude'], kde=True, color='blue')
plt.axvline(x=umbral_fraude, color='red', linestyle='--', label='Umbral de Riesgo (RF)')
plt.title('Distribución de Probabilidad de Fraude por Usuario (Random Forest)')
plt.xlabel('Probabilidad Promedio de Fraude por Usuario')
plt.ylabel('Frecuencia')
plt.legend()
plt.show()

# Visualizar la distribución de los usuarios según su riesgo de fraude (XGBoost)
plt.figure(figsize=(10, 6))
sns.histplot(fraude_por_usuario_xgb['probabilidad_fraude'], kde=True, color='green')
plt.axvline(x=umbral_fraude, color='red', linestyle='--', label='Umbral de Riesgo (XGBoost)')
plt.title('Distribución de Probabilidad de Fraude por Usuario (XGBoost)')
plt.xlabel('Probabilidad Promedio de Fraude por Usuario')
plt.ylabel('Frecuencia')
plt.legend()
plt.show()

# Reportes de clasificación a nivel transacción
print("Reporte de clasificación a nivel transacción (Random Forest):")
print(classification_report(y_test, y_pred_rf))

print("Reporte de clasificación a nivel transacción (XGBoost):")
print(classification_report(y_test2, y_pred_xgb))

# Porcentaje de fraudes detectados (Random Forest)
total_fraudes_predichos_rf = sum(y_pred_rf)
total_transacciones_rf = len(y_pred_rf)
porcentaje_fraudes_rf = (total_fraudes_predichos_rf / total_transacciones_rf) * 100
print(f'\nPorcentaje de fraudes detectados (Random Forest): {porcentaje_fraudes_rf:.2f}%')

# Porcentaje de fraudes detectados (XGBoost)
total_fraudes_predichos_xgb = sum(y_pred_xgb)
total_transacciones_xgb = len(y_pred_xgb)
porcentaje_fraudes_xgb = (total_fraudes_predichos_xgb / total_transacciones_xgb) * 100
print(f'\nPorcentaje de fraudes detectados (XGBoost): {porcentaje_fraudes_xgb:.2f}%')

# Número de transacciones fraudulentas por usuario (Random Forest)
transacciones_fraudulentas_por_usuario_rf = resultados_rf[resultados_rf['fraude_predicho'] == 1].groupby('customer_name').size()
print("\nNúmero de transacciones fraudulentas por usuario (Random Forest):")
print(transacciones_fraudulentas_por_usuario_rf)

# Número de transacciones fraudulentas por usuario (XGBoost)
transacciones_fraudulentas_por_usuario_xgb = resultados_xgb[resultados_xgb['fraude_predicho'] == 1].groupby('customer_name').size()
print("\nNúmero de transacciones fraudulentas por usuario (XGBoost):")
print(transacciones_fraudulentas_por_usuario_xgb)

# 5. Fraudes por mes
data['mes'] = data['dt_request_branch_tz'].dt.to_period('M')

# Filtrar solo las transacciones fraudulentas predichas
transacciones_fraude_rf = resultados_rf[resultados_rf['fraude_predicho'] == 1]
transacciones_fraude_xgb = resultados_xgb[resultados_xgb['fraude_predicho'] == 1]
