import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale; // Importado para consistência na formatação de números

public abstract class Produto implements Comparable<Produto>{

	private static final double MARGEM_PADRAO = 0.2;
	private static int ultimoID = 10_000;

	protected int idProduto;
	protected String descricao;
	protected double precoCusto;
	protected double margemLucro;

	private void init(String desc, double precoCusto, double margemLucro) {
		if (desc == null || desc.trim().length() < 3) {
             throw new IllegalArgumentException("Descrição inválida ou muito curta: " + desc);
        }
        if (precoCusto <= 0.0) {
             throw new IllegalArgumentException("Preço de custo deve ser positivo: " + precoCusto);
        }
        if (margemLucro <= 0.0) {
             throw new IllegalArgumentException("Margem de lucro deve ser positiva: " + margemLucro);
        }

		this.descricao = desc.trim();
		this.precoCusto = precoCusto;
		this.margemLucro = margemLucro;
		this.idProduto = ultimoID++;
	}

	protected Produto(String desc, double precoCusto, double margemLucro) {
		init(desc, precoCusto, margemLucro);
	}

	protected Produto(String desc, double precoCusto) {
		init(desc, precoCusto, MARGEM_PADRAO);
	}

	public abstract double valorDeVenda();

    @Override
	public String toString() {
    	NumberFormat moeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
		return String.format("NOME: %s: %s", descricao, moeda.format(valorDeVenda()));
	}

    @Override
    public int hashCode(){
        return idProduto;
    }

    @Override
    public boolean equals(Object obj){
        if (this == obj) return true;
        if (obj == null || !(obj instanceof Produto)) return false;
        Produto outro = (Produto) obj;
        return this.idProduto == outro.idProduto;
    }

    @Override
    public int compareTo(Produto outro){
    	return Integer.compare(this.idProduto, outro.idProduto);
    }

    public static Produto criarDoTexto(String linha) {
        if (linha == null || linha.trim().isEmpty()) {
            throw new IllegalArgumentException("Linha de dados do produto está vazia.");
        }

    	String[] dadosLinha = linha.split(";");
    	if (dadosLinha.length < 4) {
            throw new IllegalArgumentException("Formato inválido da linha de dados (mínimo 4 campos esperados): " + linha);
        }

    	int tipo;
    	String descricao;
    	double precoCusto, margemLucro;
    	LocalDate dataDeValidade;
    	Produto produto;

    	try {
            tipo = Integer.parseInt(dadosLinha[0].trim());
            descricao = dadosLinha[1].trim();
            precoCusto = Double.parseDouble(dadosLinha[2].trim().replace(",", "."));
            margemLucro = Double.parseDouble(dadosLinha[3].trim().replace(",", "."));

            if (tipo == 2) { 
                if (dadosLinha.length < 5 || dadosLinha[4].trim().isEmpty()) {
                     throw new IllegalArgumentException("Data de validade ausente ou vazia para produto perecível: " + linha);
                }
                DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                dataDeValidade = LocalDate.parse(dadosLinha[4].trim(), formatoData);
                produto = new ProdutoPerecivel(descricao, precoCusto, margemLucro, dataDeValidade);
            } else if (tipo == 1) { 
                produto = new ProdutoNaoPerecivel(descricao, precoCusto, margemLucro);
            } else {
                 throw new IllegalArgumentException("Tipo de produto inválido: " + tipo + " na linha: " + linha);
            }
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Erro ao converter número na linha: " + linha + " - " + e.getMessage());
        } catch (DateTimeParseException e) {
            throw new DateTimeParseException("Erro ao converter data (formato esperado dd/MM/yyyy) na linha: " + linha + " - " + e.getMessage(), e.getParsedString(), e.getErrorIndex());
        } catch (IllegalArgumentException e) {
             throw new IllegalArgumentException("Dados inválidos na linha: " + linha + " - " + e.getMessage());
        }

    	return produto;
    }

    public abstract String gerarDadosTexto();

    public int getIdProduto() {
        return idProduto;
    }

    public String getDescricao() {
        return descricao;
    }

    public double getPrecoCusto() {
        return precoCusto;
    }

    public double getMargemLucro() {
        return margemLucro;
    }
}