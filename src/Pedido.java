import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.text.NumberFormat;

public class Pedido implements Comparable<Pedido>{

	private static int ultimoID = 1;
	private int idPedido;

	/** Quantidade máxima de produtos de um pedido */
	public static final int MAX_PRODUTOS = 10;

	/** Porcentagem de desconto para pagamentos à vista */
	private static final double DESCONTO_PG_A_VISTA = 0.15;

	/** Vetor para armazenar os produtos do pedido */
	private Produto[] produtos;

	/** Data de criação do pedido */
	private LocalDate dataPedido;

	/** Indica a quantidade total de produtos no pedido até o momento */
	private int quantProdutos = 0;

	/** Indica a forma de pagamento do pedido sendo: 1, pagamento à vista; 2, pagamento parcelado */
	private int formaDePagamento;

	public Pedido(LocalDate dataPedido, int formaDePagamento) {
		if (dataPedido == null) {
            dataPedido = LocalDate.now();
        }
        if (formaDePagamento != 1 && formaDePagamento != 2) {
            throw new IllegalArgumentException("Forma de pagamento inválida (deve ser 1 ou 2).");
        }

		this.idPedido = ultimoID++;
		this.produtos = new Produto[MAX_PRODUTOS];
		this.quantProdutos = 0;
		this.dataPedido = dataPedido;
		this.formaDePagamento = formaDePagamento;
	}

	public boolean incluirProduto(Produto novo) {
		if (novo == null) return false;
		if (quantProdutos < MAX_PRODUTOS) {
			produtos[quantProdutos++] = novo;
			return true;
		}
		System.err.println("Atenção: Limite máximo de " + MAX_PRODUTOS + " produtos por pedido atingido.");
		return false;
	}

	public double valorFinal() {
		double valorPedido = 0;

		for (int i = 0; i < quantProdutos; i++) {
            if (produtos[i] != null) {
			    valorPedido += produtos[i].valorDeVenda();
            }
		}

		if (formaDePagamento == 1) {
			valorPedido = valorPedido * (1.0 - DESCONTO_PG_A_VISTA);
		}

		BigDecimal valorPedidoBD = BigDecimal.valueOf(valorPedido);
		valorPedidoBD = valorPedidoBD.setScale(2, RoundingMode.HALF_UP);
        return valorPedidoBD.doubleValue();
	}

	@Override
	public String toString() {
		StringBuilder stringPedido = new StringBuilder();
        NumberFormat moeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		stringPedido.append(String.format("Número do pedido: %02d\n", idPedido));
		stringPedido.append("Data do pedido: ").append(formatoData.format(dataPedido)).append("\n");
		stringPedido.append("Pedido com ").append(quantProdutos).append(" produto(s).\n");

		if (quantProdutos > 0) {
            stringPedido.append("Produtos no pedido:\n");
            for (int i = 0; i < quantProdutos; i++ ) {
                if (produtos[i] != null) {
                    stringPedido.append("  ").append(produtos[i].toString().replace("\n", "\n  ")).append("\n");
                }
            }
        }

		stringPedido.append("Pedido pago ");
		if (formaDePagamento == 1) {
			stringPedido.append("à vista. Percentual de desconto: ")
                        .append(String.format(Locale.US, "%.2f%%", DESCONTO_PG_A_VISTA * 100))
                        .append("\n");
		} else {
			stringPedido.append("parcelado.\n");
		}

		stringPedido.append("Valor total do pedido: ").append(moeda.format(valorFinal()));

		return stringPedido.toString();
	}

    @Override
    public int compareTo(Pedido outro) {
        if (outro == null) return 1;
    	return Integer.compare(this.idPedido, outro.idPedido);
    }

    // Getters
    public LocalDate getDataPedido() {
    	return dataPedido;
    }

    public int getIdPedido() {
    	return idPedido;
    }

    public int getQuantosProdutos() {
    	return quantProdutos;
    }

    public Produto[] getProdutos() {
        Produto[] copiaProdutos = new Produto[quantProdutos];
        System.arraycopy(produtos, 0, copiaProdutos, 0, quantProdutos);
    	return copiaProdutos;
    }

    public int getFormaDePagamento() {
        return formaDePagamento;
    }
}