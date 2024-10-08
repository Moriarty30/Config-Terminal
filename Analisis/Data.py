import pandas as pd

# Volvemos a cargar los datos
data = pd.read_csv('grafana_data.csv')

# Verificamos el tipo de la columna 'dt_request_branch_tz'
print("Tipo de datos de 'dt_request_branch_tz':", pd.api.types.infer_dtype(data['dt_request_branch_tz']))

# Convertir las fechas de las transacciones si no están ya en formato datetime
data['dt_request_branch_tz'] = pd.to_datetime(data['dt_request_branch_tz'], errors='coerce')

# Función 1: Usuarios con más de 3 tarjetas históricamente
def usuarios_mas_3_tarjetas(data):
    tarjetas_por_usuario = data.groupby('customer_name')['masked_pan'].nunique()
    return tarjetas_por_usuario[tarjetas_por_usuario > 3]

# Función 2: Usuarios con más de 2 IPs por día
def usuarios_mas_2_ips_dia(data):
    # Aseguramos que 'dt_request_branch_tz' esté en formato datetime
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
    trxs_por_usuario_valor = data.groupby(['customer_name', 'total_amount']).size()
    return trxs_por_usuario_valor[trxs_por_usuario_valor > 4]

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

# Ejecutamos las funciones y mostramos los resultados
print("Usuarios con más de 3 tarjetas:", usuarios_mas_3_tarjetas(data))
print("Usuarios con más de 2 IPs por día:", usuarios_mas_2_ips_dia(data))
print("Tarjetas con más de 2 usuarios:", tarjetas_mas_2_usuarios(data))
print("Más de 1.200.000 en la suma de trxs por usuario en un día:", suma_mas_1200000_dia(data))
print("Más de 5 trxs por usuario en un día:", mas_5_trxs_por_usuario_dia(data))
print("Usuarios con más de 4 trxs del mismo valor:", usuarios_mas_4_trxs_mismo_valor(data))
print("Usuarios con más de 20 trxs aprobadas en el mes:", mas_20_trxs_aprobadas_mes(data))
print("Usuarios con más de 7.030.000 trxs aprobadas en el mes:", mas_7030000_aprobadas_mes(data))
