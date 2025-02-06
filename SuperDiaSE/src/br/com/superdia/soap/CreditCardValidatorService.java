package br.com.superdia.soap;

import superdia.webservices.classes.CreditCardValidator;
import superdia.webservices.classes.CreditCardValidatorSoap;

public class CreditCardValidatorService {

    public boolean validateCard(String cardNumber, String expiryDate) {
        try {
            CreditCardValidator service = new CreditCardValidator();
            CreditCardValidatorSoap port = service.getCreditCardValidatorSoap();

            int result = port.validCard(cardNumber, expiryDate);
            System.out.println("Resultado: " + result);
            return result == 0;
        } catch (Exception e) {
            System.err.println("Erro ao validar cartão de crédito: " + e.getMessage());
            return false;
        }
    }    
}
