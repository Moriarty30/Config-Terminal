from matplotlib import pyplot as plt
import pandas as pd
from sklearn.preprocessing import LabelEncoder
from sklearn.cluster import KMeans
from sklearn.preprocessing import StandardScaler
from sklearn.metrics import silhouette_score, davies_bouldin_score, calinski_harabasz_score
from sklearn.cluster import DBSCAN
from sklearn.ensemble import IsolationForest
from sklearn.cluster import AgglomerativeClustering
from scipy.cluster.hierarchy import dendrogram, linkage
from sklearn.decomposition import PCA
import numpy as np

# Convertimos las fechas a formato datetime
data = pd.read_csv('grafana_data.csv')
data['dt_request_branch_tz'] = pd.to_datetime(data['dt_request_branch_tz'], errors='coerce')


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


#Cálculos de características adicionales (nuevas features)
sum_amounts = {}
count_transactions = {}
unique_ips = {}
total_time_diff = {}
first_transaction = {}
last_transaction = {}

for idx, row in data.iterrows():
    user = row['customer_name']
    if user not in sum_amounts:
        sum_amounts[user] = 0
        count_transactions[user] = 0
        unique_ips[user] = set()
        first_transaction[user] = row['dt_request_branch_tz']
        last_transaction[user] = row['dt_request_branch_tz']
    sum_amounts[user] += row['total_amount']
    count_transactions[user] += 1
    unique_ips[user].add(row['source_ip'])
    if row['dt_request_branch_tz'] < first_transaction[user]:
        first_transaction[user] = row['dt_request_branch_tz']
    if row['dt_request_branch_tz'] > last_transaction[user]:
        last_transaction[user] = row['dt_request_branch_tz']

# Crear nuevas columnas en el DataFrame basadas en los cálculos anteriores
data['avg_transaction_amount'] = data['customer_name'].apply(lambda x: sum_amounts[x] / count_transactions[x])
data['unique_ips'] = data['customer_name'].apply(lambda x: len(unique_ips[x]))
data['total_transactions'] = data['customer_name'].apply(lambda x: count_transactions[x])

# Calcular el tiempo entre la primera y última transacción por usuario
data['transaction_duration'] = data['customer_name'].apply(lambda x: (last_transaction[x] - first_transaction[x]).total_seconds())

# Nueva característica: Tiempo promedio entre transacciones por usuario (en segundos)
data['time_diff'] = data.groupby('customer_name')['dt_request_branch_tz'].diff().dt.total_seconds()
data['avg_time_diff'] = data.groupby('customer_name')['time_diff'].transform('mean').fillna(0)

# Nueva característica: Desviación estándar del monto de las transacciones por usuario
data['std_transaction_amount'] = data.groupby('customer_name')['total_amount'].transform('std').fillna(0)

# Nueva característica: Número de transacciones por hora del día (pico de actividad)
data['hour_of_day'] = data['dt_request_branch_tz'].dt.hour
data['transactions_per_hour'] = data.groupby(['customer_name', 'hour_of_day'])['trx_id'].transform('count')
data['transactions_per_hour'] = data['transactions_per_hour'].fillna(data['transactions_per_hour'].mean())


# Reemplazamos algunas características menos relevantes por las nuevas características
features = ['total_amount', 'masked_pan_encoded', 'source_ip_encoded', 'avg_transaction_amount',
            'unique_ips', 'avg_time_diff', 'std_transaction_amount', 'transactions_per_hour']

#print(data[features].isnull().sum())
# Escalado de los datos
scaler = StandardScaler()
X_scaled = scaler.fit_transform(data[features])

# Aplicar K-Means con 4 clusters
kmeans = KMeans(n_clusters=4, random_state=42)
data['kmeans_cluster'] = kmeans.fit_predict(X_scaled)

# Reducir la dimensionalidad para visualización con PCA
pca = PCA(n_components=2)
X_pca = pca.fit_transform(X_scaled)

# Graficar los resultados
plt.figure(figsize=(10, 6))
scatter = plt.scatter(X_pca[:, 0], X_pca[:, 1], c=data['kmeans_cluster'], cmap='viridis', s=50, alpha=0.7)
legend1 = plt.legend(*scatter.legend_elements(), title="Clusters")
plt.gca().add_artist(legend1)
plt.title('Clusters visualizados con PCA - Mejora de Features')
plt.xlabel('Componente Principal 1')
plt.ylabel('Componente Principal 2')
plt.show()

# Calcular métricas de evaluación
from sklearn.metrics import silhouette_score, davies_bouldin_score, calinski_harabasz_score

silhouette_avg = silhouette_score(X_scaled, data['kmeans_cluster'])
davies_bouldin = davies_bouldin_score(X_scaled, data['kmeans_cluster'])
calinski_harabasz = calinski_harabasz_score(X_scaled, data['kmeans_cluster'])

print(f'K-Means Metrics:')
print(f'Silhouette Score: {silhouette_avg}')
print(f'Davies-Bouldin Index: {davies_bouldin}')
print(f'Calinski-Harabasz Index: {calinski_harabasz}')

"""#Modelos no supervizado K-Means
sum_amounts = {}
count_transactions = {}

# Recorremos el dataset fila por fila
for idx, row in data.iterrows():
    user = row['customer_name']
    if user not in sum_amounts:
        sum_amounts[user] = 0
        count_transactions[user] = 0
    sum_amounts[user] += row['total_amount']
    count_transactions[user] += 1

# Crear una nueva columna 'avg_transaction_amount'
data['avg_transaction_amount'] = data['customer_name'].apply(lambda x: sum_amounts[x] / count_transactions[x])

features = ['total_amount', 'masked_pan_encoded', 'source_ip_encoded', 'avg_transaction_amount','billing_document_encode']

scaler = StandardScaler()
X_scaled = scaler.fit_transform(data[features])

kmeans = KMeans(n_clusters=5, random_state=42)
data['kmeans_cluster'] = kmeans.fit_predict(X_scaled)

silhouette_avg = silhouette_score(X_scaled, data['kmeans_cluster'])
davies_bouldin = davies_bouldin_score(X_scaled, data['kmeans_cluster'])
calinski_harabasz = calinski_harabasz_score(X_scaled, data['kmeans_cluster'])

print(f'K-Means Metrics:')
print(f'Silhouette Score: {silhouette_avg}')
print(f'Davies-Bouldin Index: {davies_bouldin}')
print(f'Calinski-Harabasz Index: {calinski_harabasz}')
"""
"""#Modelos no supervizado DBSCAN - Como que no sirvio 
features = [
    'total_amount', 'masked_pan_encoded', 'source_ip_encoded', 'total_transactions', 
    'avg_transaction_amount', 'unique_ips', 'transaction_duration', 'avg_time_diff', 
    'std_transaction_amount', 'transactions_per_hour'
]

# Normalización de las características
scaler = StandardScaler()
X_scaled = scaler.fit_transform(data[features])

# Ajuste de DBSCAN - Probar diferentes valores de eps y min_samples
dbscan = DBSCAN(eps=0.5, min_samples=5)
data['dbscan_cluster'] = dbscan.fit_predict(X_scaled)

# Calcular las métricas de rendimiento
silhouette_avg_dbscan = silhouette_score(X_scaled, data['dbscan_cluster'])
davies_bouldin_dbscan = davies_bouldin_score(X_scaled, data['dbscan_cluster'])

print(f'DBSCAN Metrics:')
print(f'Silhouette Score: {silhouette_avg_dbscan}')
print(f'Davies-Bouldin Index: {davies_bouldin_dbscan}')

# Ajustar eps utilizando el método del codo para determinar un valor óptimo
from sklearn.neighbors import NearestNeighbors
import matplotlib.pyplot as plt

# Usar el método del codo para determinar el mejor valor de 'eps'
neigh = NearestNeighbors(n_neighbors=2)
nbrs = neigh.fit(X_scaled)
distances, indices = nbrs.kneighbors(X_scaled)

# Ordenar las distancias
distances = np.sort(distances, axis=0)
distances = distances[:, 1]
plt.plot(distances)
plt.title('Método del codo para DBSCAN')
plt.xlabel('Puntos de datos')
plt.ylabel('Distancias')
plt.show()
"""

#Modelos no supervizado Isolation Forest

features = ['total_amount', 'masked_pan_encoded', 'unique_ips', 'total_transactions']

X_scaled = scaler.fit_transform(data[features])

iso_forest = IsolationForest(contamination=0.05, random_state=42)
data['isolation_forest_outlier'] = iso_forest.fit_predict(X_scaled)

outliers_count = (data['isolation_forest_outlier'] == -1).sum()
print(f'Number of outliers detected by Isolation Forest: {outliers_count}')

data['isolation_forest_outlier'] = data['isolation_forest_outlier'].apply(lambda x: 1 if x == -1 else 0)

outliers = data[data['isolation_forest_outlier'] == 1]

outlier_info = outliers[['customer_name', 'total_amount', 'source_ip', 'dt_request_branch_tz', 'billing_document']]

print(outlier_info.head(10))

#Modelos no supervizado Clustering Jerárquico
active_days = {}

for idx, row in data.iterrows():
    user = row['customer_name']
    if user not in active_days:
        active_days[user] = set()
    active_days[user].add(row['dt_request_branch_tz'].date())

data['active_days'] = data['customer_name'].apply(lambda x: len(active_days[x]))


features = ['total_amount', 'masked_pan_encoded', 'active_days', 'unique_ips']

X_scaled = scaler.fit_transform(data[features])

clustering = AgglomerativeClustering(n_clusters=4)
data['hierarchical_cluster'] = clustering.fit_predict(X_scaled)

Z = linkage(X_scaled, method='ward')
dendrogram(Z)
plt.show()

silhouette_avg_hierarchical = silhouette_score(X_scaled, data['hierarchical_cluster'])
davies_bouldin_hierarchical = davies_bouldin_score(X_scaled, data['hierarchical_cluster'])
calinski_harabasz_hierarchical = calinski_harabasz_score(X_scaled, data['hierarchical_cluster'])

print(f'Hierarchical Clustering Metrics:')
print(f'Silhouette Score: {silhouette_avg_hierarchical}')
print(f'Davies-Bouldin Index: {davies_bouldin_hierarchical}')
print(f'Calinski-Harabasz Index: {calinski_harabasz_hierarchical}')

"""
#Modelos no supervizado PCA
approved_transactions = {}
total_transactions = {}

for idx, row in data.iterrows():
    user = row['customer_name']
    if user not in approved_transactions:
        approved_transactions[user] = 0
        total_transactions[user] = 0
    if row['trx_status_id'] == 'APPROVED':
        approved_transactions[user] += 1
    total_transactions[user] += 1

data['approved_transactions'] = data['customer_name'].apply(lambda x: approved_transactions[x] / total_transactions[x])


features = ['total_amount', 'masked_pan_encoded', 'source_ip_encoded', 'approved_transactions']

X_scaled = scaler.fit_transform(data[features])

pca = PCA(n_components=2)
X_pca = pca.fit_transform(X_scaled)

data['pca1'] = X_pca[:, 0]
data['pca2'] = X_pca[:, 1]

plt.scatter(data['pca1'], data['pca2'], c=data['kmeans_cluster'], cmap='viridis')
plt.title('Visualización de Clusters con PCA')
plt.show()
"""