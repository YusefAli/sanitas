package com.mycorp.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycorp.support.Poliza;
import com.mycorp.support.PolizaBasicoFromPolizaBuilder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import portalclientesweb.ejb.interfaces.PortalClientesWebEJBRemote;
import util.datos.PolizaBasico;
import util.datos.UsuarioAlta;

@Service
public class ClienteService {

    /**
     * The Constant LOG.
     */
    private static final Logger LOG = LoggerFactory.getLogger(ClienteService.class);
    private static final String ESCAPED_LINE_SEPARATOR = "\\n";

    @Value("#{envPC['tarjetas.getDatos']}")
    public String TARJETAS_GETDATOS = "";

    /**
     * The portalclientes web ejb remote.
     */
    @Autowired
    // @Qualifier("portalclientesWebEJB")
    private PortalClientesWebEJBRemote portalclientesWebEJBRemote;

    /**
     * The rest template.
     */
    @Autowired
    @Qualifier("restTemplateUTF8")
    private RestTemplate restTemplate;


    private ObjectMapper mapper = new ObjectMapper();


    public String getId(UsuarioAlta usuarioAlta, StringBuilder clientName, StringBuilder datosServicio) {
        String idCliente = null;
        // Obtiene el idCliente de la tarjeta
        if (StringUtils.isNotBlank(usuarioAlta.getNumTarjeta())) {
            try {
                String urlToRead = TARJETAS_GETDATOS + usuarioAlta.getNumTarjeta();
                ResponseEntity<String> res = restTemplate.getForEntity(urlToRead, String.class);
                if (res.getStatusCode() == HttpStatus.OK) {
                    String dusuario = res.getBody();
                    clientName.append(dusuario);
                    idCliente = dusuario;
                    datosServicio.append("Datos recuperados del servicio de tarjeta:").append(ESCAPED_LINE_SEPARATOR).append(mapper.writeValueAsString(dusuario));
                }
            } catch (Exception e) {
                LOG.error("Error al obtener los datos de la tarjeta", e);
            }
        } else if (StringUtils.isNotBlank(usuarioAlta.getNumPoliza())) {
            try {
                Poliza poliza = new Poliza();
                poliza.setNumPoliza(Integer.valueOf(usuarioAlta.getNumPoliza()));
                poliza.setNumColectivo(Integer.valueOf(usuarioAlta.getNumDocAcreditativo()));
                poliza.setCompania(1);

                PolizaBasico polizaBasicoConsulta = new PolizaBasicoFromPolizaBuilder().withPoliza(poliza).build();

                final util.datos.DetallePoliza detallePolizaResponse = portalclientesWebEJBRemote.recuperarDatosPoliza(polizaBasicoConsulta);

                clientName.append(detallePolizaResponse.getTomador().getNombre()).
                        append(" ").
                        append(detallePolizaResponse.getTomador().getApellido1()).
                        append(" ").
                        append(detallePolizaResponse.getTomador().getApellido2());

                idCliente = detallePolizaResponse.getTomador().getIdentificador();
                datosServicio.append("Datos recuperados del servicio de tarjeta:").append(ESCAPED_LINE_SEPARATOR).append(mapper.writeValueAsString(detallePolizaResponse));
            } catch (Exception e) {
                LOG.error("Error al obtener los datos de la poliza", e);
            }
        }
        return idCliente;
    }

}