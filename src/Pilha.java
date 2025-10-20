import java.util.NoSuchElementException;

public class Pilha<E> {

	private Celula<E> topo;
	private Celula<E> fundo; 

	public Pilha() {
		Celula<E> sentinela = new Celula<E>();
		fundo = sentinela;
		topo = sentinela; 
	}

	public boolean vazia() {
		return (fundo == topo);
	}

	public void empilhar(E item) {
        if (item == null) return; 
		Celula<E> novaCelula = new Celula<E>(item, topo);
		topo = novaCelula; 
	}

	public E desempilhar() {
		if (vazia()) {
			throw new NoSuchElementException("Nao há nenhum item na pilha para desempilhar!");
		}
		E desempilhado = topo.getItem(); 
		Celula<E> proximoDoTopo = topo.getProximo(); 
        topo.setProximo(null); 
		topo = proximoDoTopo; 
		return desempilhado;
	}

	public E consultarTopo() {
		if (vazia()) {
			throw new NoSuchElementException("Nao há nenhum item na pilha para consultar!");
		}
		return topo.getItem();
	}

	/**
	 * Cria e devolve uma nova pilha contendo os primeiros numItens elementos
	 * do topo da pilha atual. Os elementos são mantidos na mesma ordem
	 * em que estavam na pilha original (o topo da original é o topo da nova).
	 * A pilha original não é modificada.
	 *
	 * @param numItens o número de itens a serem copiados do topo da pilha original.
	 * @return uma nova instância de Pilha<E> contendo os numItens primeiros elementos.
	 * @throws IllegalArgumentException se numItens for negativo ou se a pilha não contém numItens elementos.
	 */
	public Pilha<E> subPilha(int numItens) {
		if (numItens < 0) {
            throw new IllegalArgumentException("Quantidade de itens não pode ser negativa.");
        }
        if (numItens == 0) {
            return new Pilha<E>();
        }

        int tamanhoAtual = 0;
        Celula<E> temp = topo;
        while (temp != fundo) {
            tamanhoAtual++;
            temp = temp.getProximo();
        }
        if (numItens > tamanhoAtual) {
             throw new IllegalArgumentException("A pilha contém apenas " + tamanhoAtual + " elementos, não é possível obter " + numItens + ".");
        }

        Pilha<E> subPilha = new Pilha<>();
        Pilha<E> pilhaAuxiliar = new Pilha<>(); 

        Celula<E> atual = this.topo;
        for (int i = 0; i < numItens && atual != fundo; i++) {
            pilhaAuxiliar.empilhar(atual.getItem());
            atual = atual.getProximo();
        }

        while (!pilhaAuxiliar.vazia()) {
            subPilha.empilhar(pilhaAuxiliar.desempilhar());
        }

		return subPilha;
	}

    public void mostrar() {
        System.out.print("Pilha: Topo -> [ ");
        Celula<E> atual = topo;
        while (atual != fundo) {
            System.out.print(atual.getItem() + (atual.getProximo() != fundo ? ", " : ""));
            atual = atual.getProximo();
        }
        System.out.println(" ] <- Base");
    }
}