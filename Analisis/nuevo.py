import pandas as pd
from sklearn.preprocessing import LabelEncoder
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier, GradientBoostingClassifier
from sklearn.metrics import classification_report, confusion_matrix
from collections import defaultdict
import seaborn as sns
import matplotlib.pyplot as plt

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

# Funciones de etiquetado de riesgo (las funciones de riesgo se mantienen)
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

    data['customer_name'] = data['customer_name'].str.lower().str.strip()
    return data


def usuarios_unicos(data):
    #data = data.drop_duplicates(subset=['customer_name'], keep='last')
    data = data.drop_duplicates(subset=['billing_document'], keep='last')
    return data


data = etiquetar_usuarios_riesgo(data)

#data = agrupar_usuarios_unicos(data)


data['riesgo'] = data['riesgo'].apply(lambda x: 1 if x >= 0.75 else 0)

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

# División en conjunto de entrenamiento y prueba
X_train2, X_test2, y_train2, y_test2 = train_test_split(X2, y2, test_size=0.2, random_state=42)
# División en conjunto de entrenamiento y prueba
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

# Entrenar Random Forest
clf_rf = RandomForestClassifier(random_state=42)
clf_rf.fit(X_train, y_train)
y_pred_rf = clf_rf.predict(X_test)
# Reporte de clasificación
print("Random Forest Classification Report:")
print(classification_report(y_test, y_pred_rf))

#Entrenar XGBoost
clf = GradientBoostingClassifier(n_estimators=100, learning_rate=1.0,max_depth=3).fit(X_train2, y_train2)
clf.score(X_test2, y_test2)
y_pred2 = clf.predict(X_test2)
print("Reporte de clasificación para el modelo XGBoost:")
print(classification_report(y_test2, y_pred2))

# Generar las probabilidades de fraude para cada transacción
y_proba_rf = clf_rf.predict_proba(X_test)[:, 1]  
y_pred2_fr = clf.predict_proba(X_test2)[: , 1]


"""# Matriz de confusión
cm_rf = confusion_matrix(y_test, y_pred_rf)
sns.heatmap(cm_rf, annot=True, fmt='d', cmap='Blues')
plt.title('Matriz de Confusión - Random Forest')
plt.show()

#Matriz de confusión para XGBoost
cm_rf2 = confusion_matrix(y_test2, y_pred2)
sns.heatmap(cm_rf2, annot=True, fmt='d', cmap='Blues')
plt.title('Matriz de Confusión - XGBoost')
plt.show()"""

# Creación del DataFrame con los resultados
resultados = pd.DataFrame({
    'customer_name': data.loc[X_test.index, 'customer_name'],
    'billing_document': data.loc[X_test.index, 'billing_document'],
    'masked_pan': data.loc[X_test.index, 'masked_pan'],
    'probabilidad_fraude': y_proba_rf,
    'fraude_predicho': y_pred_rf
})

resultados2 = pd.DataFrame({
    'customer_name': data.loc[X_test2.index, 'customer_name'],
    'billing_document': data.loc[X_test2.index, 'billing_document'],
    'masked_pan': data.loc[X_test2.index, 'masked_pan'],
    'probabilidad_fraude': y_pred2_fr,
    'fraude_predicho': y_pred2
})


# 1. Usuarios con mayor probabilidad de fraude
usuarios_fraude_riesgo = resultados[resultados['fraude_predicho'] >= 0.75].sort_values(by='probabilidad_fraude', ascending=False)
print("\nUsuarios con mayor probabilidad de fraude:")
print(usuarios_fraude_riesgo.head(10))

usuarios_fraude_riesgo2 = resultados2[resultados2['fraude_predicho'] >= 0.75].sort_values(by='probabilidad_fraude', ascending=False)
print("\nUsuarios con mayor probabilidad de fraude2:")
print(usuarios_fraude_riesgo2.head(10))

# 2. Listar las 10 transacciones con mayor probabilidad de fraude
transacciones_fraude_riesgo = resultados.sort_values(by='probabilidad_fraude', ascending=False).head(10)
print("\nLas 10 transacciones con mayor probabilidad de fraude:")
print(transacciones_fraude_riesgo[['customer_name','billing_document', 'masked_pan', 'probabilidad_fraude']])

transacciones_fraude_riesgo2 = resultados2.sort_values(by='probabilidad_fraude', ascending=False).head(10)
print("\nLas 10 transacciones con mayor probabilidad de fraude2:")
print(transacciones_fraude_riesgo2[['customer_name','billing_document','masked_pan', 'probabilidad_fraude']])


# 3. Porcentaje de fraude detectado
total_fraudes_predichos = sum(y_pred_rf)
total_transacciones = len(y_pred_rf)
porcentaje_fraudes = (total_fraudes_predichos / total_transacciones) * 100
print(f'\nPorcentaje de fraudes detectados: {porcentaje_fraudes:.2f}%')


total_fraudes_predichos2 = sum(y_pred2_fr)
total_transacciones2 = len(y_pred2_fr)
porcentaje_fraudes2 = (total_fraudes_predichos2 / total_transacciones2) * 100
print(f'\nPorcentaje de fraudes detectados2: {porcentaje_fraudes2:.2f}%')

# 4. Número de transacciones fraudulentas por usuario
fraudes_por_usuario = resultados[resultados['fraude_predicho'] == 1].groupby('customer_name').size()
print("\nNúmero de transacciones fraudulentas por usuario:")
print(fraudes_por_usuario)

fraudes_por_usuario2 = resultados2[resultados2['fraude_predicho'] == 1].groupby('customer_name').size()
print("\nNúmero de transacciones fraudulentas por usuario2:")
print(fraudes_por_usuario2)

# 5. Fraudes por mes
data['mes'] = data['dt_request_branch_tz'].dt.to_period('M')

# Crear el DataFrame de resultados de predicciones con la columna 'mes'
resultados = pd.DataFrame({
    'customer_name': data.loc[X_test.index, 'customer_name'],
    'billing_document': data.loc[X_test.index, 'billing_document'],
    'probabilidad_fraude': y_proba_rf,
    'fraude_predicho': y_pred_rf,
    'mes': data.loc[X_test.index, 'mes']
})

resultados2 = pd.DataFrame({
    'customer_name': data.loc[X_test2.index, 'customer_name'],
    'billing_document': data.loc[X_test2.index, 'billing_document'],
    'masked_pan': data.loc[X_test2.index, 'masked_pan'],
    'probabilidad_fraude': y_pred2_fr,
    'fraude_predicho': y_pred2,
    'mes': data.loc[X_test2.index, 'mes']
})

# Filtrar solo las transacciones fraudulentas predichas
transacciones_fraude = resultados[resultados['fraude_predicho'] == 1]

transacciones_fraude2 = resultados2[resultados2['fraude_predicho'] == 1]

# Contar fraudes por mes
fraudes_por_mes = transacciones_fraude.groupby('mes').size()
fraudes_por_mes2 = transacciones_fraude2.groupby('mes').size()

"""# Graficar fraudes por mes
plt.figure(figsize=(10, 6))
fraudes_por_mes.plot(kind='bar', color='salmon')
plt.title('Fraudes Detectados por Mes')
plt.xlabel('Mes')
plt.ylabel('Número de Fraudes')
plt.show()

plt.figure(figsize=(10, 6))
fraudes_por_mes2.plot(kind='bar', color='salmon')
plt.title('Fraudes Detectados por Mes')
plt.xlabel('Mes')
plt.ylabel('Número de Fraudes')
plt.show()
"""