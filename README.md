# ChickenTest

Este es un proyecto de ejemplo que utiliza Java con Spring Boot, MySQL, Thymeleaf y Bootstrap para crear una aplicación web relacionada con la gestión de pollos y huevos en una granja.

## Descripción

El proyecto "ChickenTest" es una aplicación web que te permite llevar un registro de pollos y huevos en una granja. Utiliza tecnologías como Spring Boot para el backend, Thymeleaf para las plantillas HTML y Bootstrap para la interfaz de usuario.

## Características

- Registro y seguimiento de pollos y huevos.
- Compra y venta de pollos y huevos.
- Interfaz de usuario atractiva y receptiva con Bootstrap.
- Dockerizada para facilitar la ejecución y despliegue.
- Conexión a una base de datos MySQL para el almacenamiento de datos.

## Requisitos
- Docker instalado en tu máquina local.

## Instrucciones de uso

1. Clona este repositorio en tu máquina local.
2. Abre una terminal y navega a la carpeta raíz del proyecto.
3. Ejecuta el siguiente comando para levantar la aplicación y la base de datos:
   `C:\user\myDesktop> cd /ruta/al/proyecto/chickenTest`
   `C:\user\myDesktop\ruta\al\proyecto\chickenTest> docker-compose up -d`
4. Abre un navegador web y navega a `http://localhost:8080` para acceder a la aplicación.
5. Para acceder a la base de datos, ejecutar el siguiente comando: `docker exec -it chickenTest_db mysql -u brian -p`
6. password a ingresar: brian

## Endpoints de la API

-- ENDPOINT PARA CHICKEN --

Obtener todos los pollos registrados en la granja:
http://localhost:8080/api/chicken

Obtener un pollo por ID:
http://localhost:8080/api/chicken/{id}

-- ENDPOINT PARA EGG --

Obtener todos los huevos registrados en la granja:
http://localhost:8080/api/egg

Obtener un huevo por ID:
http://localhost:8080/api/egg/{id}

-- ENDPOINT PARA FARM --

Obtener los datos de la granja:
http://localhost:8080/api/farm

Obtener los datos de la granja por ID:
http://localhost:8080/api/farm/{id}

Obtener un resumen de las propiedades de la granja:
http://localhost:8080/api/farm/dashboard/resume

Obtener el progreso y estadísticas de la granja:
   http://localhost:8080/api/farm/dashboard/progress

Obtener las transacciones de la granja:
   http://localhost:8080/api/farm/dashboard/transactions

Obtener el dinero disponible en la granja: 
http://localhost:8080/api/farm/dashboard/cash-available
 
## Contribuciones

Las contribuciones son bienvenidas. Si encuentras errores o mejoras, no dudes en abrir un "issue" o enviar un "pull request".
