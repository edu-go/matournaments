### MA Tournaments DEMO

### Antes de ejecutar el proyecto, verificar tener instalado:
* Java 21
* Maven 3.9.9
* Docker (opcional, para ejecutar la base de datos en un contenedor)
* docker-compose (opcional, para ejecutar la base de datos en un contenedor)

### Para ejecutar el proyecto:
* docker-compose up -d (desde la raiz del proyecto, para iniciar la base de datos en un contenedor Docker)
* mvn -DskipTests clean package
* java -jar target/swing-auth-demo-1.0.0.jar