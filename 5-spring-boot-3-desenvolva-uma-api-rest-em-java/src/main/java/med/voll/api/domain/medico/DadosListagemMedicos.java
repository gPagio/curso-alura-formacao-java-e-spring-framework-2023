package med.voll.api.domain.medico;

public record DadosListagemMedicos(Long id, String nome, String email, String telefone, String crm, Especialidade especialidade) {

    public DadosListagemMedicos(Medico medico){
        this(medico.getId(), medico.getNome(), medico.getEmail(), medico.getTelefone(), medico.getCrm(), medico.getEspecialidade());
    }

}
