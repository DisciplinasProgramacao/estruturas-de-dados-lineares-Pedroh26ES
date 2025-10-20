import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class ProdutoPerecivel extends Produto {

	private static final double DESCONTO = 0.25;
	private static final long PRAZO_DESCONTO_DIAS = 7;
	private LocalDate dataDeValidade;

	public ProdutoPerecivel(String desc, double precoCusto, double margemLucro, LocalDate validade) {
		super(desc, precoCusto, margemLucro);
		if (validade == null) {
            throw new IllegalArgumentException("Data de validade não pode ser nula.");
        }
        LocalDate hoje = LocalDate.now();
		if (validade.isBefore(hoje)) {
            System.err.println("Atenção: Produto '" + desc + "' cadastrado com data de validade vencida (" + validade.format(DateTimeFormatter.ISO_DATE) + ").");
		}
		this.dataDeValidade = validade;
	}

	public ProdutoPerecivel(String desc, double precoCusto, LocalDate validade) {
		super(desc, precoCusto);
		if (validade == null) {
            throw new IllegalArgumentException("Data de validade não pode ser nula.");
        }
        LocalDate hoje = LocalDate.now();
        if (validade.isBefore(hoje)) {
             System.err.println("Atenção: Produto '" + desc + "' cadastrado com data de validade vencida (" + validade.format(DateTimeFormatter.ISO_DATE) + ").");
		}
		this.dataDeValidade = validade;
	}

	@Override
	public double valorDeVenda() {
		LocalDate hoje = LocalDate.now();

		if (dataDeValidade.isBefore(hoje)) {
			return 0.0;
		}

		double precoVenda = super.precoCusto * (1.0 + super.margemLucro);
		long diasAteValidade = ChronoUnit.DAYS.between(hoje, dataDeValidade);

		if (diasAteValidade <= PRAZO_DESCONTO_DIAS) {
			precoVenda = precoVenda * (1.0 - DESCONTO);
		}

        return Math.round(precoVenda * 100.0) / 100.0;
	}

    @Override
    public String toString(){
        DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dadosBase = super.toString();
        String validadeStr = "Válido até " + formatoData.format(dataDeValidade);

        LocalDate hoje = LocalDate.now();
        long diasAteValidade = ChronoUnit.DAYS.between(hoje, dataDeValidade);

        if (diasAteValidade < 0) {
            validadeStr += " (VENCIDO)";
        } else if (diasAteValidade <= PRAZO_DESCONTO_DIAS) {
             validadeStr += " (Promoção - Próx. Vencimento)";
        }

        return dadosBase + "\n" + validadeStr;
    }

	@Override
    public String gerarDadosTexto() {
		String precoCustoFormatado = String.format(Locale.US, "%.2f", precoCusto);
		String margemLucroFormatada = String.format(Locale.US, "%.2f", margemLucro);
		DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String dataFormatada = formatoData.format(dataDeValidade);

		return String.format("2;%s;%s;%s;%s", descricao, precoCustoFormatado, margemLucroFormatada, dataFormatada);
	}

    public LocalDate getDataDeValidade() {
        return dataDeValidade;
    }
}