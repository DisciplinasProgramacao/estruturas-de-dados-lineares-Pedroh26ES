import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Predicate;

public class Fila<E> {

	private Celula<E> primeiro;
	private Celula<E> ultimo;

	public Fila() {
		primeiro = new Celula<E>();
		ultimo = primeiro;
	}

	public boolean vazia() {
		return (primeiro == ultimo);
	}

	public void inserir(E item) {
		if (item == null) return; 
		Celula<E> novaCelula = new Celula<E>(item);
		ultimo.setProximo(novaCelula);
		ultimo = novaCelula;
	}

	public E remover() {
		if (vazia()) {
			throw new NoSuchElementException("Erro: A fila está vazia!");
		}
		Celula<E> celulaRemovida = primeiro.getProximo();
		E itemRemovido = celulaRemovida.getItem();
		primeiro.setProximo(celulaRemovida.getProximo());
		if (celulaRemovida == ultimo) {
			ultimo = primeiro;
		}
        celulaRemovida.setProximo(null); 
		return itemRemovido;
	}

	public E consultarPrimeiro() {
		if (vazia()) {
			throw new NoSuchElementException("Erro: A fila está vazia!");
		}
		return primeiro.getProximo().getItem();
	}

	public void mostrar() {
		System.out.print("Fila: [ ");
		Celula<E> atual = primeiro.getProximo();
		while (atual != null) {
			System.out.print(atual.getItem() + (atual.getProximo() != null ? ", " : ""));
			atual = atual.getProximo();
		}
		System.out.println(" ]");
	}

	public double calcularValorMedio(Function<E, Double> extrator, int quantidade) {
		if (vazia() || quantidade <= 0 || extrator == null) {
			return 0.0;
		}
		double soma = 0.0;
		int contador = 0;
		Celula<E> atual = primeiro.getProximo();
		while (atual != null && contador < quantidade) {
			E item = atual.getItem();
			if (item != null) {
				try {
					Double valor = extrator.apply(item);
                    if (valor != null) { 
					    soma += valor;
					    contador++;
                    }
				} catch (Exception e) {
					System.err.println("Erro ao aplicar extrator no item: " + item + " - " + e.getMessage());
				}
			}
			atual = atual.getProximo();
		}
		return (contador == 0) ? 0.0 : soma / contador;
	}

	public Fila<E> filtrar(Predicate<E> condicional, int quantidade) {
		Fila<E> filaFiltrada = new Fila<>();
		if (vazia() || quantidade <= 0 || condicional == null) {
			return filaFiltrada;
		}
		int contador = 0;
		Celula<E> atual = primeiro.getProximo();
		while (atual != null && contador < quantidade) {
			E item = atual.getItem();
			if (item != null) {
				try {
					if (condicional.test(item)) {
						filaFiltrada.inserir(item); 
					}
				} catch (Exception e) {
					System.err.println("Erro ao aplicar condição no item: " + item + " - " + e.getMessage());
				}
			}
			contador++; 
			atual = atual.getProximo();
		}
		return filaFiltrada;
	}
}