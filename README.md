Twitter API – Backend (Spring Boot)

API REST que simula el núcleo de una red social tipo Twitter: usuarios, publicaciones, comentarios, likes y relaciones de “seguir / dejar de seguir”.

Implementada con Spring Boot 3, Spring Security + JWT, JPA / Hibernate y MySQL, organizada en capas claramente separadas.

1. Tecnologías y dependencias principales

Java 17

Spring Boot 3.5.7

spring-boot-starter-web

spring-boot-starter-data-jpa

spring-boot-starter-validation

spring-boot-starter-security

MySQL (driver mysql-connector-j)

Lombok (anotaciones como @Data, @AllArgsConstructor, etc.)

MapStruct (mappers entre entidades y DTOs)

JJWT (io.jsonwebtoken) para generación y validación de tokens JWT

Swagger / OpenAPI 3 para documentación de la API (configurada con OpenApiConfig)

2. Arquitectura por capas y estructura de paquetes

La organización de src/main/java sigue un patrón clásico por capas:

org.vedruna.twitterapi
├── TwitterapiApplication.java
├── controller
│   ├── dto
│   ├── converter
│   ├── handler
│   ├── impl
│   ├── validation
│   └── (controllers REST: User, Publication, Comment, Like, Follow)
├── persistance
│   ├── entity
│   └── repository
├── service
│   ├── exception
│   └── impl
└── security
    ├── config
    ├── controller
    │   ├── converter
    │   └── dto
    ├── filter
    └── service

2.1. Capa de presentación – controller

Responsable de exponer los endpoints REST y trabajar con DTOs:

controller

UserController / UserControllerImpl (usuarios, registro, login básico, following / followers)

PublicationController (interfaz) + PublicationControllerImpl (publicaciones)

CommentController (comentarios)

LikeController (likes)

FollowController (seguir / dejar de seguir)

controller/dto

CreateUserDto, LoginDto, TokenDto, UpdateUsernameDto, UserDto

CreatePublicationDto, UpdatePublicationDto, PublicationDto

CreateCommentDto, CommentDto

controller/converter

UserConverter (MapStruct, mapea UserEntity ↔ UserDto)

PublicationConverter (MapStruct, mapea PublicationEntity ↔ PublicationDto/CreatePublicationDto/UpdatePublicationDto)

controller/handler

HandlerExceptionController – @RestControllerAdvice centralizado para transformar excepciones en respuestas HTTP con ProblemDetail.

controller/validation

ExistingUser / ExistingUserValidator – anotación y validador custom para comprobar existencia de usuarios en ciertas operaciones.

2.2. Capa de persistencia – persistance

Responsable del acceso a datos y mapeo ORM con JPA/Hibernate:

persistance/entity

UserEntity – usuarios (implementa UserDetails para integrarse con Spring Security).

Campos principales:

id, username, password, email, description, createDate

role: RoleEntity

publications: List<PublicationEntity>

following: Set<UserEntity> (usuarios a los que sigue)

followers: Set<UserEntity> (usuarios que le siguen)

RoleEntity – roles / autoridades.

PublicationEntity – publicaciones:

id, user (autor), text, createDate, updateDate

CommentEntity – comentarios:

id, text, createDate, user, publication

LikeEntity – likes:

id, user, publication, createDate

persistance/repository

Repositorios JPA: UserRepository, RoleRepository, PublicationRepository, CommentRepository, LikeRepository.

Extienden típicamente de JpaRepository y definen consultas específicas para:

Buscar usuarios por username/email.

Obtener publicaciones por usuario.

Calcular timeline en base a relaciones de seguimiento.

Contar likes por publicación, etc.

2.3. Capa de servicio – service

Contiene la lógica de negocio y las reglas de dominio:

Interfaces de servicio:

AuthService (versión simple de autenticación para pruebas)

UserService

PublicationService

CommentService

LikeService

FollowService

RoleService

Implementaciones en service/impl:

AuthServiceImpl

Implementación simple y temporal que valida credenciales contra la BD y devuelve un TokenDto con un UUID como token (no JWT completo).

Lanza AuthenticationFailedException y UserNotFoundException según corresponda.

UserServiceImpl

Registro y gestión de usuarios.

Encapsula validaciones de negocio (p.ej. conflicto de email/username).

PublicationServiceImpl

CRUD de publicaciones (createPublication, updatePublication, deletePublication, etc.).

Uso intensivo de paginación (Page y Pageable).

Maneja las fechas (createDate, updateDate) y el enlace con el usuario autor.

Lanza PublicationNotFoundException y PublicationConflictException.

CommentServiceImpl

Añadir comentarios a una publicación (addComment).

Obtener lista de comentarios por publicación (getCommentsByPublication).

LikeServiceImpl

Añadir/retirar likes.

Evita duplicados y gestiona conflictos en base a usuario + publicación.

FollowServiceImpl

Gestiona relaciones de seguimiento entre usuarios (follow/unfollow).

Utiliza las colecciones bidireccionales followers y following de UserEntity.

RoleServiceImpl

Búsqueda y gestión de roles.

service/exception

Excepciones específicas de dominio:

UserNotFoundException, RoleNotFoundException, PublicationNotFoundException, FollowNotFoundException

UsernameConflictException, EmailConflictException, PublicationConflictException

AuthenticationFailedException

2.4. Capa de seguridad – security

Gestiona la autenticación JWT y la configuración de Spring Security:

security/config

ApplicationConfig

UserDetailsService que carga UserEntity desde UserRepository.

PasswordEncoder (BCrypt).

AuthenticationProvider (DaoAuthenticationProvider).

AuthenticationManager expuesto como bean.

SecurityConfig

Configura la cadena de filtros (SecurityFilterChain).

Sesiones STATELESS (JWT: no se usa sesión de servidor).

Inserta JwtAuthenticationFilter antes de UsernamePasswordAuthenticationFilter.

Define permisos por ruta:

Públicos:

OPTIONS /** (CORS preflight)

POST /api/v1/users/register

POST /api/v1/users/login

GET /api/v1/users/by-username/**

GET /api/v1/publications/user/**

GET /api/v1/comments/**

GET /api/v1/likes/**

/v3/api-docs/**, /swagger-ui/**, /swagger-ui.html

/api/v1/auth/** y /auth/** (controlador de seguridad)

Protegidos (requieren JWT):

Resto de endpoints (authenticated()).

Configuración de CORS (CorsConfigurationSource).

security/filter

JwtAuthenticationFilter

Intercepta cada petición.

Lee el header Authorization: Bearer <token>.

Valida el token vía JWTServiceImpl.

Si es válido, construye la autenticación y la introduce en el SecurityContext.

security/service

JWTServiceImpl

Genera y valida JWT:

Secret y tiempo de expiración cargados de application.yml (auth.access-token-secret-key, auth.access-token-expiration).

Permite extraer claims, subject (username), fechas de caducidad, etc.

AuthService (en este paquete, a diferencia de service.AuthService)

Servicio de autenticación “completo”:

Login con AuthenticationManager y generación de tokens JWT (access/refresh).

Registro asignando rol por defecto.

Renovación de access token vía refresh token.

security/controller

AuthController – Endpoints de autenticación basados en JWT:

Base path: /api/v1/auth

POST /register

POST /login

POST /refresh

GET /me (devuelve información del usuario autenticado).

OpenApiConfig – Configura el esquema de seguridad Bearer para OpenAPI/Swagger.

converter y dto

UserDTO, AuthResponseDTO, LoginRequestDTO, RefreshRequestDTO, RegisterRequestDTO… DTOs específicos del flujo de autenticación JWT.

⚠️ Nota importante:
Actualmente conviven dos sistemas de autenticación:

El “simple” en service.AuthServiceImpl usado por UserController (/api/v1/users/login) que devuelve un TokenDto con un UUID.

El completo basado en JWT en el paquete security (/api/v1/auth/**), integrado con JwtAuthenticationFilter y SecurityConfig.

Para un entorno real, lo recomendable es usar solo el sistema JWT (/api/v1/auth/**) y eliminar/refactorizar el de pruebas.

3. Configuración – application.yml

Ubicado en src/main/resources/application.yml:

Datos de la aplicación:

spring.application.name

spring.application.author

Conexión a MySQL:

spring.datasource.url=jdbc:mysql://localhost:3306/apitwitter?...

spring.datasource.username=root

spring.datasource.password=root

JPA / Hibernate:

spring.jpa.hibernate.ddl-auto=validate

La BD debe existir y coincidir con el esquema (schema.sql); no se crea automáticamente.

JWT:

auth.access-token-secret-key=...

auth.access-token-expiration=180000 (ms)

Management / Actuator (configurado, aunque no está el starter en el pom):

Configuración de health, métricas (management.metrics.*), tracing y export Prometheus.

4. Base de datos y scripts SQL

En src/main/resources/db:

schema.sql

Crea el esquema apitwitter y las tablas:

roles

users

publications

comments

likes

Tabla de relación users_follow_users (self join para seguir/seguidores)

data.sql

Inserta datos de ejemplo:

Roles: Administrador, Usuario

Varios usuarios de ejemplo (admin, juan01, maria2025)

Publicaciones de ejemplo vinculadas a usuarios

Relaciones de seguimiento (users_follow_users)

Likes y comentarios (si están incluidos en el archivo completo)

5. Modelado de dominio (resumen)
5.1. Usuario (UserEntity)

Implementa UserDetails para Spring Security.

Relaciones:

role: RoleEntity – un usuario tiene un rol.

publications: List<PublicationEntity> – publicaciones creadas.

following: Set<UserEntity> – usuarios a los que sigue.

followers: Set<UserEntity> – usuarios que le siguen.

Detalles de seguridad:

getAuthorities() construye una colección de GrantedAuthority a partir del rol.

Métodos isAccountNonExpired, isAccountNonLocked, etc. devuelven true (cuenta siempre activa en esta implementación).

5.2. Publicación (PublicationEntity)

Campos:

id, user, text, createDate, updateDate

Cada publicación pertenece a un usuario (ManyToOne).

Usada en timeline, listados y operaciones CRUD.

5.3. Comentario (CommentEntity)

Campos:

id, text, createDate, user, publication

Cada comentario pertenece a:

Un usuario (autor).

Una publicación.

5.4. Like (LikeEntity)

Campos:

id, user, publication, createDate

Representa el “me gusta” de un usuario sobre una publicación.

A nivel de negocio, se evita duplicar likes para la misma pareja usuario–publicación.

5.5. Rol (RoleEntity)

Campos:

id, name, users

Conecta con UserEntity para construir las autoridades de Spring Security.

6. DTOs y conversión (MapStruct)

La API nunca devuelve directamente las entidades JPA, sino distintos DTOs:

Usuarios

CreateUserDto – datos de entrada para POST /api/v1/users/register.

LoginDto – credenciales para el login básico.

TokenDto – respuesta del login simple (token UUID).

UpdateUsernameDto – para cambiar el username.

UserDto – datos públicos del usuario (sin contraseña).

Publicaciones

CreatePublicationDto – crear nueva publicación.

UpdatePublicationDto – actualizar contenido.

PublicationDto – respuesta con:

Datos de la publicación.

userId, username del autor, etc.

Comentarios

CreateCommentDto

CommentDto

Seguridad (JWT)

RegisterRequestDTO, LoginRequestDTO, AuthResponseDTO, RefreshRequestDTO, UserDTO (en paquete security.controller.dto).

Los conversores (UserConverter, PublicationConverter) usan MapStruct para traducir entre entidades y DTOs, incluyendo mapeos específicos de IDs y nombres de usuario.

7. Endpoints principales
7.1. Usuarios – /api/v1/users

Controlador: UserController (interfaz) + UserControllerImpl (implementación).

Base path: /api/v1/users

Método	Ruta	Público / privado	Descripción
POST	/register	Público	Registro de usuario. Recibe CreateUserDto, devuelve UserDto.
POST	/login	Público	Login “simple” de pruebas. Recibe LoginDto, devuelve TokenDto (UUID).
PATCH	/{userId}/username	Privado	Cambiar el username del usuario indicado.
GET	/by-username/{username}	Público	Obtener datos de un usuario por su username (UserDto).
GET	/{userId}/following	Privado	Página de usuarios a los que sigue el usuario (Page<UserDto>).
GET	/{userId}/followers	Privado	Página de usuarios que siguen al usuario (Page<UserDto>).

Los métodos privados se protegen mediante Spring Security y JWT según la configuración de SecurityConfig.

7.2. Autenticación JWT – /api/v1/auth

Controlador: security.controller.AuthController.

Base path: /api/v1/auth

Método	Ruta	Público / privado	Descripción
POST	/register	Público	Registra un usuario y devuelve tokens (access + refresh).
POST	/login	Público	Autentica y devuelve AuthResponseDTO con tokens JWT.
POST	/refresh	Público	Recibe un refresh token y devuelve nuevo access token.
GET	/me	Privado (JWT)	Devuelve los datos del usuario autenticado (UserDTO).

Para consumir estos endpoints:

En login/register se obtiene un access token (y probablemente un refresh token).

Los endpoints protegidos se invocan con el header:

Authorization: Bearer <access_token>

7.3. Publicaciones – /api/v1/publications

Controlador: PublicationController (interfaz) + PublicationControllerImpl.

Base path: /api/v1/publications

(Los métodos usan Pageable para paginar, por ejemplo: ?page=0&size=10&sort=createDate,desc)

Método	Ruta	Público / privado	Descripción
GET	/	Privado	Obtiene todas las publicaciones (paginado).
GET	/user/{userId}	Público	Publicaciones de un usuario concreto (paginado).
GET	/timeline/{userId}	Privado	Timeline: publicaciones de los usuarios a los que sigue userId.
POST	"" o /	Privado	Crear nueva publicación a partir de CreatePublicationDto.
PUT	/{publicationId}	Privado	Actualizar una publicación existente (UpdatePublicationDto).
DELETE	/{publicationId}	Privado	Borrar una publicación.

La implementación (PublicationControllerImpl) delega en PublicationServiceImpl, que se encarga de:

Comprobar la existencia del usuario autor.

Asignar timestamps (createDate, updateDate).

Lanzar excepciones de dominio si algo falla.

7.4. Comentarios – /api/v1/comments

Controlador: CommentController.

Base path: /api/v1/comments

Método	Ruta	Público / privado	Descripción
POST	/{publicationId}/user/{userId}	Normalmente privado (según configuración de seguridad)	Crea un comentario del usuario userId en la publicación publicationId. Recibe CreateCommentDto.
GET	/{publicationId}	Público	Lista de comentarios de una publicación (List<CommentDto>).
7.5. Likes – /api/v1/likes

Controlador: LikeController.

Base path: /api/v1/likes

Método	Ruta	Público / privado	Descripción
POST	/{publicationId}/user/{userId}	Privado	Hacer like a la publicación publicationId por parte de userId.
DELETE	/{publicationId}/user/{userId}	Privado	Quitar el like de la publicación por parte del usuario.
GET	/{publicationId}/count	Público	Número total de likes de la publicación.
7.6. Seguimiento (follow) – /api/v1/follows

Controlador: FollowController.

Base path: /api/v1/follows

Método	Ruta	Público / privado	Descripción
POST	/{userId}/follow/{toFollowId}	Privado	Crea la relación “userId sigue a toFollowId”.
DELETE	/{userId}/follow/{toUnfollowId}	Privado	Elimina la relación de seguimiento.
GET	/{userId}/following	Privado	Devuelve lista de usuarios que userId está siguiendo.
GET	/{userId}/followers	Privado	Devuelve lista de usuarios que siguen a userId.

Observación: también existen endpoints similares en UserController para obtener following/followers pero con paginación (Page<UserDto>).
Los de FollowController devuelven List<UserDto> y se apoyan directamente en FollowService y UserRepository.

8. Validación y manejo de errores
8.1. Validación

Se usa Jakarta Bean Validation (spring-boot-starter-validation):

Anotaciones como @NotBlank, @Size, @Email en los DTOs (CreateUserDto, CreatePublicationDto, etc.).

Anotaciones personalizadas:

@ExistingUser – valida que un userId referido en una operación apunte a un usuario que realmente existe en BD.

Cuando la validación falla:

Se lanza MethodArgumentNotValidException.

HandlerExceptionController captura esta excepción y devuelve un objeto ProblemDetail con:

Código de estado HTTP adecuado (por ejemplo 400).

Detalles de campos con errores.

8.2. Manejo centralizado de excepciones

HandlerExceptionController (@RestControllerAdvice) captura:

Excepciones de dominio (UserNotFoundException, PublicationNotFoundException, etc.).

Errores de JPA (DataIntegrityViolationException, etc.).

Errores de conversión de parámetros (MethodArgumentTypeMismatchException).

Errores HTTP típicos (HttpRequestMethodNotSupportedException, HttpMessageNotReadableException).

Configura para cada caso:

Código HTTP apropiado (404, 409, 400, 500, etc.).

Mensaje detallado en formato ProblemDetail / JSON.

Esto garantiza un formato de error uniforme en toda la API.

9. Paginación

Varios endpoints usan Pageable (de Spring Data):

Ejemplo: getAllPublications(Pageable pageable), getPublicationsByUser, getFollowing, getFollowers, etc.

Se puede controlar desde el cliente con parámetros estándar:

?page=0&size=10&sort=createDate,desc

La capa de servicio delega la paginación en los repositorios (Page<T>).

10. Cómo ejecutar el proyecto
10.1. Requisitos previos

Java 17 instalado y configurado en JAVA_HOME.

Maven instalado.

MySQL en ejecución (por defecto en localhost:3306).

10.2. Pasos

Crear la base de datos ejecutando los scripts en MySQL:

SOURCE src/main/resources/db/schema.sql;
SOURCE src/main/resources/db/data.sql;


Revisar src/main/resources/application.yml y adaptar:

spring.datasource.url

spring.datasource.username

spring.datasource.password

Compilar y ejecutar:

mvn clean install
mvn spring-boot:run


La aplicación se levantará por defecto en http://localhost:8080.

(Opcional) Abrir Swagger UI, normalmente en:

http://localhost:8080/swagger-ui/index.html

11. Ejemplos rápidos de uso
11.1. Registro de usuario (JWT)
POST /api/v1/auth/register
Content-Type: application/json

{
  "username": "nuevoUsuario",
  "email": "nuevo@example.com",
  "password": "MiPasswordSegura123",
  "description": "Mi bio"
}


Respuesta: AuthResponseDTO con tokens.

11.2. Crear publicación
POST /api/v1/publications
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "text": "Esta es mi primera publicación desde la API"
}

12. Extensibilidad

Para añadir nuevas funcionalidades siguiendo el diseño actual:

Entidad nueva o campo nuevo

Modificar / añadir clases en persistance/entity.

Actualizar schema.sql + migración en BD.

Crear/actualizar repositorios en persistance/repository.

Lógica de negocio

Declarar métodos en la interfaz correspondiente de service.

Implementarlos en service/impl.

Exponer por API

Crear / ampliar un controlador en controller.

Definir DTOs en controller/dto.

Añadir mappings en converter (MapStruct).

Documentar con anotaciones Swagger (@Operation, @ApiResponses, etc.).

Seguridad

Ajustar permisos en SecurityConfig (requestMatchers).

Si el endpoint es privado, acceder siempre con Authorization: Bearer <token>.