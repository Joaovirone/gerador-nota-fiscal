package com.NFS_E.notaFiscalEletronica;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class NotaFiscalEletronicaApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotaFiscalEletronicaApplication.class, args);
	}

}
