package med.voll.api.domain.consulta.validacoes.cancelamento;

import med.voll.api.domain.ValidacaoException;
import med.voll.api.domain.consulta.ConsultaRepository;
import med.voll.api.domain.consulta.DadosCancelamentoConsulta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component("ValidadorHorarioAntecedenciaCancelamento")
public class ValidadorHorarioAntecedencia implements ValidadorCancelamentoConsulta {

    @Autowired
    private ConsultaRepository consultaRepository;

    @Override
    public void validar(DadosCancelamentoConsulta dados) {
        var dataConsulta = consultaRepository.getReferenceById(dados.idConsulta()).getData();
        var agora = LocalDateTime.now();
        var diferencaEmHoras = Duration.between(agora, dataConsulta).toHours();

        if (diferencaEmHoras < 24) {
            throw new ValidacaoException("Consulta só pode ser cancelada com antecedência mínima de 24 horas");
        }
    }
}
