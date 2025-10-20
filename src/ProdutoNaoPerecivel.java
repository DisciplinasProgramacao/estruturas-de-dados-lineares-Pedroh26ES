import java.util.Locale;

public class ProdutoNaoPerecivel extends Produto{

	public ProdutoNaoPerecivel(String desc, double precoCusto, double margemLucro) {
		super(desc, precoCusto, margemLucro);
	}

	public ProdutoNaoPerecivel(String desc, double precoCusto) {
		super(desc, precoCusto);
	}

	@Override
	public double valorDeVenda() {
        // Arredondamento para 2 casas decimais
        double valor = super.precoCusto * (1.0 + super.margemLucro);
		return Math.round(valor * 100.0) / 100.0;
	}

	@Override
    public String gerarDadosTexto() {
		String precoCustoFormatado = String.format(Locale.US, "%.2f", precoCusto);
		String margemLucroFormatada = String.format(Locale.US, "%.2f", margemLucro);

		return String.format("1;%s;%s;%s", descricao, precoCustoFormatado, margemLucroFormatada);
    }
}