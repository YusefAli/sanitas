package com.mycorp;

import com.mycorp.support.DatosCliente;
import com.mycorp.support.ValueCode;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;
import util.datos.UsuarioAlta;

import java.util.Arrays;


/**
 * Unit test for simple App.
 */
@RunWith(MockitoJUnitRunner.class)
public class RealizarSimulacionTest {


    @Mock
    RestTemplate restTemplate;

    @InjectMocks
    @Spy
    ZendeskService service;

    DatosCliente datosClienteMock;

    UsuarioAlta usuarioAlta;


    @Before
    public void prepareTests() {

        service.CLIENTE_GETDATOS = "test.CLIENTE_GETDATOS";
        service.PETICION_ZENDESK = "%s %s %s";
        service.TARJETAS_GETDATOS = "test.TARJETAS_GETDATOS";
        service.TOKEN_ZENDESK = "test.TOKEN_ZENDESK";
        service.URL_ZENDESK = "http://localhost";//"http://localhost/api/v2/tickets.json"
        service.ZENDESK_ERROR_DESTINATARIO = "test.ZENDESK_ERROR_DESTINATARIO";
        service.ZENDESK_ERROR_MAIL_FUNCIONALIDAD = "99";
        service.ZENDESK_USER = "test.ZENDESK_USER";

        datosClienteMock=new DatosCliente();
        datosClienteMock.setGenTTipoCliente(1);
        datosClienteMock.setFechaNacimiento("04/05/1984");
        datosClienteMock.setGenCTipoDocumento(1);


        usuarioAlta=new UsuarioAlta();
        usuarioAlta.setNumTarjeta("111111");

    }


    @Test
    public void testApp() throws Exception {
        ValueCode valueCode = new ValueCode();
        valueCode.setCode("testCode");
        valueCode.setValue("testValue");
        Mockito.when(service.getTiposDocumentosRegistro()).thenReturn(Arrays.asList(valueCode, valueCode));

        Mockito.when(restTemplate.getForObject(Mockito.anyString(), Matchers.any(Class.class), Mockito.anyString())
        ).thenReturn(datosClienteMock);
        String altaTicketZendesk = service.altaTicketZendesk(usuarioAlta, "yus");
        Assert.assertEquals(altaTicketZendesk, "Nº tarjeta Sanitas o Identificador: 111111\\nTipo documento: 0\\nNº documento: null\\nEmail personal: null\\nNº móvil: null\\nUser Agent: yus\\n\\nDatos recuperados de BRAVO:\\n\\nTeléfono: null\\nFeha de nacimiento: 04/05/1984\\nNúmero documento: null\\nTipo cliente: POTENCIAL\\nID estado del cliente: null\\nID motivo de alta cliente: null\\nRegistrado: Sí\\n\\n");
    }

}
