Twitter API Clone - Proyecto Backend

Este proyecto es una API RESTful tipo microblog, inspirada en Twitter, desarrollada con:

Spring Boot

Spring Security

JWT

MongoDB/PostgreSQL (según configuración)

El proyecto está organizado siguiendo buenas prácticas de arquitectura por capas, uso de DTOs, validaciones y seguridad, con documentación automática a través de Swagger/OpenAPI.

El objetivo es proporcionar un backend completo para:

Gestión de usuarios

Gestión de publicaciones

Gestión de relaciones entre usuarios (seguidores/following)

Autenticación segura mediante JWT

📑 Tabla de Contenidos

Estructura General del Proyecto

Capas del Proyecto

Controller

DTOs

Converter

Service

Persistence

Security

Autenticación y Seguridad

Endpoints y Funcionalidades

Swagger / OpenAPI

Cómo Ejecutar el Proyecto

Notas de Buenas Prácticas

📂 Estructura General del Proyecto
src/
 └─ main/
     └─ java/
         └─ org.vedruna.twitterapi/
             ├─ controller/               # Controladores REST
             │   ├─ dto/                  # DTOs usados por los controladores
             │   ├─ FollowController.java
             │   ├─ PublicationController.java
             │   └─ UserController.java
             ├─ persistance/             # Capa de persistencia (Entities y Repositorios)
             │   ├─ entity/
             │   └─ repository/
             ├─ service/                 # Lógica de negocio
             │   ├─ FollowService.java
             │   ├─ AuthService.java
             │   └─ JWTServiceImpl.java
             └─ security/                # Configuración y lógica de seguridad
                 ├─ config/
                 ├─ controller/
                 ├─ controller/dto/
                 ├─ converter/
                 ├─ filter/
                 └─ service/

🏗 Capas del Proyecto
Controller

Exponen los endpoints REST.

Se comunican con la capa Service y usan DTOs para recibir y enviar datos.

Controladores principales:

Controlador	Funcionalidad
FollowController	Gestión de relaciones de usuario (follow/unfollow, followers/following)
PublicationController	CRUD de publicaciones, timeline y publicaciones por usuario
UserController	Gestión de usuarios (registro, login, consulta, edición username)
AuthController	Registro/login, refresh token, info del usuario autenticado

Características:

Responden con ResponseEntity para controlar los códigos HTTP.

Validaciones con Hibernate Validator (@NotBlank, @Email, etc.).

Métodos privados requieren JWT para autorización.

DTOs

Los DTOs permiten transferir datos entre capas sin exponer entidades completas ni passwords.

Usuarios (controller/dto):

DTO	Uso
CreateUserDto	Registro de usuario
LoginDto	Login de usuario
UpdateUsernameDto	Edición de username
UserDto	Datos públicos del usuario
TokenDto	Token de autenticación

Publicaciones (controller/dto):

DTO	Uso
CreatePublicationDto	Crear o actualizar publicación
UpdatePublicationDto	Editar publicación
PublicationDto	Respuesta de publicaciones

Seguridad (security/controller/dto):

DTO	Uso
RegisterRequestDTO	Registro de usuario
LoginRequestDTO	Login de usuario
RefreshRequestDTO	Renovación de token
AuthResponseDTO	Respuesta con access + refresh token
UserDTO	Info pública del usuario autenticado

Ventajas de usar DTOs:

Validación centralizada (@NotBlank, @Email, @Size).

Evitan exponer información sensible.

Documentación automática con Swagger (@Schema).

Converter

UserConverter (security/controller/converter):

Convierte entre UserEntity y los DTOs de seguridad (UserDTO, LoginRequestDTO, RegisterRequestDTO).

Mantiene la lógica de conversión centralizada y facilita la reutilización de código.

Service
Servicio	Funcionalidad
AuthService	login, register y refresh token
JWTServiceImpl	Generación y validación de JWT (Access + Refresh), obtención de claims, expiraciones y validaciones
FollowService	Gestión de relaciones follow/unfollow, obtención de followers/following
Persistence


Entities:

Entity	Descripción
UserEntity	Información de usuarios, relación con roles y followers
RoleEntity	Roles de usuario (Usuario/Admin)
PublicationEntity	Publicaciones (texto, timestamps, autor)

Repositories:

Extienden JpaRepository o CrudRepository.

Incluyen métodos CRUD y queries personalizadas (UserRepository, RoleRepository, PublicationRepository).

Security

Config:

SecurityConfig → Filtros, endpoints públicos y privados.

ApplicationConfig → Beans de seguridad (UserDetailsService, AuthenticationProvider, PasswordEncoder).

Filter:

JwtAuthenticationFilter → Extrae y valida JWT, setea contexto de seguridad.

Service:

JWTServiceImpl → Generación, validación y extracción de claims de JWT.

Controller:

AuthController → Endpoints de login, registro, refresh token y datos de usuario autenticado.

Converter:

UserConverter → Conversión entre entidades y DTOs para AuthController.

🔒 Autenticación y Seguridad

JWT Authentication

Access token: corta duración, enviado en header Authorization: Bearer <token>

Refresh token: larga duración, se usa para renovar access token

Roles

Usuario estándar: "Usuario"

Admin (opcional)

Filtros

JwtAuthenticationFilter valida JWT en cada petición

Spring Security

Configura endpoints públicos y privados

Stateless (SessionCreationPolicy.STATELESS)

🚀 Endpoints y Funcionalidades
Usuarios
Endpoint	Método	Descripción	Acceso
/api/v1/users/register	POST	Crear usuario	Público
/api/v1/users/login	POST	Login y recibir tokens	Público
/api/v1/users/{userId}/username	PATCH	Actualizar username	Privado
/api/v1/users/by-username/{username}	GET	Buscar usuario por username	Público
/api/v1/users/{userId}/following	GET	Obtener usuarios que sigue	Privado
/api/v1/users/{userId}/followers	GET	Obtener seguidores	Privado
Publicaciones
Endpoint	Método	Descripción	Acceso
/api/v1/publications/	GET	Todas las publicaciones	Privado
/api/v1/publications/user/{userId}	GET	Publicaciones de un usuario	Público
/api/v1/publications/timeline/{userId}	GET	Timeline de publicaciones	Privado
/api/v1/publications/	POST	Crear publicación	Privado
/api/v1/publications/{publicationId}	PUT	Editar publicación	Privado
/api/v1/publications/{publicationId}	DELETE	Eliminar publicación	Privado
Auth (JWT)
Endpoint	Método	Descripción	Acceso
/api/v1/auth/register	POST	Registrar usuario	Público
/api/v1/auth/login	POST	Login usuario	Público
/api/v1/auth/refresh	POST	Renovar access token	Público
/api/v1/auth/me	GET	Obtener info usuario autenticado	Privado
📜 Swagger / OpenAPI

Documentación automática.

Acceso: /swagger-ui.html o /swagger-ui/index.html

DTOs y endpoints documentados con:

@Schema para campos

@Operation y @ApiResponses para endpoints

⚙️ Cómo Ejecutar el Proyecto

Configurar application.properties o application.yml:

# JWT
auth.access-token-secret-key=<clave-base64>
auth.access-token-expiration=600000
auth.refresh-token-secret-key=<clave-base64>
auth.refresh-token-expiration=3600000

# Base de datos
spring.datasource.url=...
spring.datasource.username=...
spring.datasource.password=...


Build del proyecto:

mvn clean install


Ejecutar:

mvn spring-boot:run


Probar endpoints con Postman o Swagger UI.

✅ Notas de Buenas Prácticas


Separación clara de capas: Controller → Service → Repository → Entity

Uso de DTOs para seguridad y validación

Seguridad JWT stateless con roles y filtros

Documentación completa con Javadoc y Swagger

Lógica de negocio limpia, sin mezclar con seguridad o persistencia