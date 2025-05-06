**Proyecto rinWS - Universidad de Ibagué**

Este proyecto fue desarrollado para implementar una comunicación con el web service de 
Bancolombia, y de esta manera:

*   Automatizar el registro de pagos en el sistema de información de la universidad
*   Remover el tiempo de espera de 24h, para el desbloqueo en el SIA de
los estudiantes que pagaron su matrícula por medio de Bancolombia
*   Ofrecer mayores facilidades de pago a los diferentes usuarios de los 
servicios de la universidad, entre otros.

El proyecto consiste en un web service SOAP, desarrollado con [Spring Boot](http://projects.spring.io/spring-boot/) y [Apache CXF](http://cxf.apache.org/). 
Para poder ejecutar el proyecto, es necesario ejecutar el comando "mvn clean generate-sources", 
el cual genera las clases necesarias para implementar un endpoint del web service, basado en los 
diferentes métodos expuestos en el wsld, así como proveer los archivos properties con la siguiente
información: 

*   credenciales de conexión a la base de datos (tanto para main como para test)
*   datos por defecto para la construcción de objectos del web service




Si deseas agregar o actualizar certificados de firmas puedes seguir estos pasos:

En el archivo certificado.jks agregar certificado con keytool de java.


Agregar nuevo certificado

keytool -import -trustcacerts -alias alias.bancolombia -file certificado.cer -keystore certificado.jks

Después se debe crear el paquete y subir al servidor.

Nota: Para más información en el repositorio de confluence encontrarás la documentación completa del proceso a seguir

