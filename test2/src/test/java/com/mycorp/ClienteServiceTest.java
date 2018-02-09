package com.mycorp;

import com.mycorp.services.ClienteService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import util.datos.UsuarioAlta;


/**
 * Unit test for simple App.
 */
@RunWith(MockitoJUnitRunner.class)
public class ClienteServiceTest {


    @Mock
    RestTemplate restTemplate;

    @InjectMocks
    @Spy
    ClienteService clienteService;


    UsuarioAlta usuarioAlta;


    @Before
    public void prepareTests() {

        clienteService.TARJETAS_GETDATOS = "test.TARJETAS_GETDATOS";

        usuarioAlta = new UsuarioAlta();
        usuarioAlta.setNumTarjeta("111111");

    }


    @Test
    public void testClienteService() throws Exception {

        Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Matchers.any(Class.class))
        ).thenReturn(new ResponseEntity("testClienteServiceResponse", HttpStatus.OK));
        String altaTicketZendesk = clienteService.getId(usuarioAlta, new StringBuilder().append("yus"), new StringBuilder());
        Assert.assertEquals(altaTicketZendesk, "testClienteServiceResponse");
    }


}
