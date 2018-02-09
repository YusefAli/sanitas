package com.mycorp.services;

import com.mycorp.support.DatosCliente;
import com.mycorp.support.ValueCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import util.datos.UsuarioAlta;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

@Service
public class BravoService {

    /**
     * The Constant LOG.
     */
    private static final Logger LOG = LoggerFactory.getLogger(BravoService.class);
    private static final String ESCAPED_LINE_SEPARATOR = "\\n";
    @Autowired
    ClienteService clienteService;
    private SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    @Autowired
    @Qualifier("restTemplateUTF8")
    private RestTemplate restTemplate;

    public StringBuilder getDatosBravo(UsuarioAlta usuarioAlta, StringBuilder clientName, StringBuilder datosServicio) {

        String idCliente = clienteService.getId(usuarioAlta, clientName, datosServicio);

        StringBuilder datosBravo = new StringBuilder();
        try {
            datosBravo.append(ESCAPED_LINE_SEPARATOR + "Datos recuperados de BRAVO:" + ESCAPED_LINE_SEPARATOR + ESCAPED_LINE_SEPARATOR);
            // Obtenemos los datos del cliente
            DatosCliente cliente = restTemplate.getForObject("http://localhost:8080/test-endpoint", DatosCliente.class, idCliente);

            datosBravo.append("Teléfono: ").append(cliente.getGenTGrupoTmk()).append(ESCAPED_LINE_SEPARATOR);


            datosBravo.append("Feha de nacimiento: ").append(formatter.format(formatter.parse(cliente.getFechaNacimiento()))).append(ESCAPED_LINE_SEPARATOR);

            List<ValueCode> tiposDocumentos = getTiposDocumentosRegistro();
            for (int i = 0; i < tiposDocumentos.size(); i++) {
                if (tiposDocumentos.get(i).getCode().equals(cliente.getGenCTipoDocumento().toString())) {
                    datosBravo.append("Tipo de documento: ").append(tiposDocumentos.get(i).getValue()).append(ESCAPED_LINE_SEPARATOR);
                }
            }
            datosBravo.append("Número documento: ").append(cliente.getNumeroDocAcred()).append(ESCAPED_LINE_SEPARATOR);

            datosBravo.append("Tipo cliente: ");
            switch (cliente.getGenTTipoCliente()) {
                case 1:
                    datosBravo.append("POTENCIAL").append(ESCAPED_LINE_SEPARATOR);
                    break;
                case 2:
                    datosBravo.append("REAL").append(ESCAPED_LINE_SEPARATOR);
                    break;
                case 3:
                    datosBravo.append("PROSPECTO").append(ESCAPED_LINE_SEPARATOR);
                    break;
            }

            datosBravo.append("ID estado del cliente: ").append(cliente.getGenTStatus()).append(ESCAPED_LINE_SEPARATOR);

            datosBravo.append("ID motivo de alta cliente: ").append(cliente.getIdMotivoAlta()).append(ESCAPED_LINE_SEPARATOR);

            datosBravo.append("Registrado: ").append((cliente.getfInactivoWeb() == null ? "Sí" : "No")).append(ESCAPED_LINE_SEPARATOR + ESCAPED_LINE_SEPARATOR);


        } catch (Exception e) {
            LOG.error("Error al obtener los datos en BRAVO del cliente", e);
        }
        return datosBravo;
    }


    public List<ValueCode> getTiposDocumentosRegistro() {
        return Arrays.asList(new ValueCode(), new ValueCode()); // simulacion servicio externo
    }

}