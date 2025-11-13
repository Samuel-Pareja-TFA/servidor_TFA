package org.vedruna.twitterapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal para arrancar la aplicación Spring Boot de Twitter API.
 *
 * <p>Responsabilidades:
 * <ul>
 *   <li>Configurar y lanzar el contexto de Spring Boot.</li>
 *   <li>Habilitar el escaneo de componentes en el paquete base
 *       {@code org.vedruna.twitterapi} y sus subpaquetes.</li>
 * </ul>
 *
 * <p>Notas de implementación:
 * <ul>
 *   <li>La anotación {@link SpringBootApplication} combina varias
 *       configuraciones esenciales para una aplicación Spring Boot,
 *       incluyendo {@code @Configuration}, {@code @EnableAutoConfiguration}
 *       y {@code @ComponentScan}.</li>
 *   <li>El método {@code main} utiliza {@link SpringApplication#run} para
 *       iniciar la aplicación, lo que configura automáticamente el
 *       entorno, carga los beans y arranca el servidor embebido.</li>
 * </ul>
 */
@SpringBootApplication
public class TwitterapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(TwitterapiApplication.class, args);
	}

}
