package com.mycorp;

import com.mycorp.services.BravoService;
import com.mycorp.services.ClienteService;
import com.mycorp.services.ZendeskService;
import com.mycorp.support.DatosCliente;
import com.mycorp.utils.Zendesk;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;
import util.datos.UsuarioAlta;


/**
 * Unit test for simple App.
 */
@RunWith(MockitoJUnitRunner.class)
public class RealizarSimulacionTest {


    @Mock
    RestTemplate restTemplate;

    @Mock
    Zendesk zendesk;

    @Mock
    ClienteService clienteService;

    @Mock
    BravoService bravoService;

    @InjectMocks
    @Spy
    ZendeskService service;

    DatosCliente datosClienteMock;

    UsuarioAlta usuarioAlta;


    @Before
    public void prepareTests() {

        service.CLIENTE_GETDATOS = "test.CLIENTE_GETDATOS";
        service.PETICION_ZENDESK = "%s %s %s";

        service.ZENDESK_ERROR_DESTINATARIO = "test.ZENDESK_ERROR_DESTINATARIO";
        service.ZENDESK_ERROR_MAIL_FUNCIONALIDAD = "99";

        zendesk = new Zendesk("test.TOKEN_ZENDESK", "http://localhost", "test.ZENDESK_USER");

        clienteService.TARJETAS_GETDATOS = "test.TARJETAS_GETDATOS";

        datosClienteMock = new DatosCliente();
        datosClienteMock.setGenTTipoCliente(1);
        datosClienteMock.setFechaNacimiento("04/05/1984");
        datosClienteMock.setGenCTipoDocumento(1);


        usuarioAlta = new UsuarioAlta();
        usuarioAlta.setNumTarjeta("111111");

    }


    @Test
    public void testZendeskService() throws Exception {
        Mockito.when(clienteService.getId(Matchers.any(UsuarioAlta.class), Matchers.any(StringBuilder.class), Matchers.any(StringBuilder.class)))
                .thenReturn("testClienteServiceResponse");

        Mockito.when(bravoService.getDatosBravo(Matchers.any(UsuarioAlta.class), Matchers.any(StringBuilder.class), Matchers.any(StringBuilder.class)))
                .thenReturn(new StringBuilder().append("\\nDatos recuperados de BRAVO:\\n\\nTeléfono: null\\nFeha de nacimiento: 04/05/1984\\nNúmero documento: null\\nTipo cliente: POTENCIAL\\nID estado del cliente: null\\nID motivo de alta cliente: null\\nRegistrado: Sí\\n\\n"));

        Mockito.when(restTemplate.getForObject(Mockito.anyString(), Matchers.any(Class.class), Mockito.anyString())
        ).thenReturn(datosClienteMock);
        String altaTicketZendesk = service.altaTicketZendesk(usuarioAlta, "yus");
        Assert.assertEquals(altaTicketZendesk.toString(), "Nº tarjeta Sanitas o Identificador: 111111\\nTipo documento: 0\\nNº documento: null\\nEmail personal: null\\nNº móvil: null\\nUser Agent: yus\\n\\nDatos recuperados de BRAVO:\\n\\nTeléfono: null\\nFeha de nacimiento: 04/05/1984\\nNúmero documento: null\\nTipo cliente: POTENCIAL\\nID estado del cliente: null\\nID motivo de alta cliente: null\\nRegistrado: Sí\\n\\n");
    }


}
