import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.text.NumberFormat; 
public class App {

	static String nomeArquivoDados = "produtos.txt";
    static Scanner teclado;
    static Produto[] produtosCadastrados;
    static int quantosProdutos = 0;
    static Fila<Pedido> filaPedidos = new Fila<>();
    static Pedido pedidoAtual = null;

    static void limparTela() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    static void pausa() {
        System.out.println("\nDigite Enter para continuar...");
        try {
            teclado.nextLine();
        } catch (Exception e) {
        }
    }

    static void cabecalho() {
        System.out.println("\n=============================");
        System.out.println("    AEDs II - LOJA LEGAL     ");
        System.out.println("=============================");
    }

     static int lerInteiro(String mensagem) {
        int valor = -1;
        boolean valido = false;
        while (!valido) {
            System.out.print(mensagem);
            String entrada = teclado.nextLine();
            try {
                valor = Integer.parseInt(entrada.trim());
                valido = true;
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor, digite um número inteiro.");
            }
        }
        return valor;
    }


    static int menu() {
        // limparTela(); // Comentar se atrapalhar depuração
        cabecalho();
        System.out.println("1 - Listar todos os produtos");
        System.out.println("2 - Buscar produto por código");
        System.out.println("3 - Buscar produto por nome");
        System.out.println("4 - Iniciar novo pedido");
        System.out.println("5 - Incluir produto no pedido atual");
        System.out.println("6 - Finalizar pedido atual");
        System.out.println("7 - Listar produtos dos próximos pedidos na fila");
        System.out.println("0 - Sair");

        return lerInteiro("Digite sua opção: ");
    }

    static Produto[] lerProdutos(String nomeArquivo) {
    	File arquivoProdutos = new File(nomeArquivo);
    	Produto[] produtosLidos = null;
        int numProdutosEsperados = 0;
        int produtosCarregados = 0;

        try (Scanner arquivo = new Scanner(arquivoProdutos, StandardCharsets.UTF_8)) {
            if (!arquivo.hasNextLine()) {
                 System.err.println("Erro: Arquivo de produtos está vazio ou ilegível.");
                 return new Produto[0];
            }
            try {
                numProdutosEsperados = Integer.parseInt(arquivo.nextLine().trim());
            } catch (NumberFormatException e) {
                 System.err.println("Erro: A primeira linha do arquivo não contém um número válido de produtos.");
                 return new Produto[0];
            }

            if (numProdutosEsperados <= 0) {
                 System.err.println("Aviso: Número de produtos no cabeçalho é zero ou negativo.");
                 return new Produto[0];
            }

    		produtosLidos = new Produto[numProdutosEsperados];

    		while (arquivo.hasNextLine() && produtosCarregados < numProdutosEsperados) {
    			String linha = arquivo.nextLine();
                if (linha.trim().isEmpty()) continue;

                try {
    			    Produto produto = Produto.criarDoTexto(linha);
                    boolean idDuplicado = false;
                    for (int i = 0; i < produtosCarregados; i++) {
                        if (produtosLidos[i].hashCode() == produto.hashCode()) {
                            System.err.println("Aviso: ID de produto duplicado ("+ produto.hashCode() +") encontrado e ignorado na linha: " + linha);
                            idDuplicado = true;
                            break;
                        }
                    }
                    if (!idDuplicado) {
    			        produtosLidos[produtosCarregados] = produto;
                        produtosCarregados++;
                    }
                // ****** CORREÇÃO: Ordem dos catch blocks ******
                } catch (NumberFormatException e) { // Captura erros de conversão numérica primeiro
                     System.err.println("Erro de formato numérico ao ler produto da linha: '" + linha + "' - " + e.getMessage());
                } catch (DateTimeParseException e) { // Captura erros de conversão de data
                     System.err.println("Erro no formato da data ao ler produto da linha: '" + linha + "' - " + e.getMessage());
                } catch (IllegalArgumentException e) { // Captura outros erros de validação (inclui os de Produto.init)
                     System.err.println("Erro de dados inválidos ao ler produto da linha: '" + linha + "' - " + e.getMessage());
                }
    		}
            quantosProdutos = produtosCarregados;

            if (produtosCarregados < numProdutosEsperados) {
                 System.err.println("Aviso: Esperava-se " + numProdutosEsperados + " produtos, mas apenas " + produtosCarregados + " foram carregados com sucesso.");
            }
             if (produtosCarregados > 0) {
                System.out.println(quantosProdutos + " produtos carregados do arquivo '" + nomeArquivo + "'.");
            }

    	} catch (IOException e) {
    		System.err.println("Erro crítico ao ler o arquivo de produtos '" + nomeArquivo + "': " + e.getMessage());
            quantosProdutos = 0;
            return new Produto[0];
    	}

    	return (produtosLidos != null) ? produtosLidos : new Produto[0];
    }

    static Produto buscarProdutoPorCodigo() {
        cabecalho();
        System.out.println("--- Buscar Produto por Código ---");
        if (quantosProdutos == 0) {
            System.out.println("Nenhum produto cadastrado.");
            return null;
        }
        int idProduto = lerInteiro("Digite o código do produto: ");

        for (int i = 0; i < quantosProdutos; i++) {
        	if (produtosCadastrados[i] != null && produtosCadastrados[i].hashCode() == idProduto) {
        		return produtosCadastrados[i];
        	}
        }
        System.out.println("Produto com código " + idProduto + " não encontrado.");
        return null;
    }

    static Produto buscarProdutoPorNome() {
        cabecalho();
        System.out.println("--- Buscar Produto por Nome ---");
         if (quantosProdutos == 0) {
            System.out.println("Nenhum produto cadastrado.");
            return null;
        }
    	System.out.print("Digite o nome (ou parte do nome) do produto: ");
        String descricaoBusca = teclado.nextLine().trim();

        if (descricaoBusca.isEmpty()) {
             System.out.println("Nome para busca não pode ser vazio.");
             return null;
        }

        Produto encontrado = null;
        int contagem = 0;

        for (int i = 0; i < quantosProdutos; i++) {
        	if (produtosCadastrados[i] != null && produtosCadastrados[i].getDescricao().equalsIgnoreCase(descricaoBusca)) {
                System.out.println("\nProduto encontrado (correspondência exata):");
                mostrarProdutoEncontrado(produtosCadastrados[i]);
        		return produtosCadastrados[i];
    		}
        }

        System.out.println("\nBusca exata não encontrada. Procurando por produtos que contenham '" + descricaoBusca + "':");
        Pilha<Produto> encontrados = new Pilha<>();
         for (int i = 0; i < quantosProdutos; i++) {
        	if (produtosCadastrados[i] != null && produtosCadastrados[i].getDescricao().toLowerCase().contains(descricaoBusca.toLowerCase())) {
                 System.out.printf(" - [%d] %s\n", produtosCadastrados[i].hashCode(), produtosCadastrados[i].getDescricao());
                 encontrados.empilhar(produtosCadastrados[i]);
        		 contagem++;
    		}
        }

        if (contagem == 0) {
            System.out.println("Nenhum produto encontrado contendo '" + descricaoBusca + "'.");
            return null;
        } else if (contagem == 1) {
             encontrado = encontrados.desempilhar(); // Como só tem 1, desempilha ele
             System.out.println("\nProduto encontrado:");
             mostrarProdutoEncontrado(encontrado);
             return encontrado;
        } else {
             System.out.println("\n" + contagem + " produtos encontrados. Use a busca por código (mostrado entre []) para selecionar.");
             return null;
        }
    }

    private static void mostrarProdutoEncontrado(Produto produto) {
        if (produto != null){
            System.out.println("--- Detalhes do Produto ---");
            System.out.println(produto.toString());
            System.out.println("Código: " + produto.hashCode());
            // ****** CORREÇÃO: Locale e NumberFormat ******
            NumberFormat formatadorMoeda = NumberFormat.getCurrencyInstance(Locale.of("pt", "BR"));
            System.out.println("Preço Custo: " + formatadorMoeda.format(produto.getPrecoCusto()));
            System.out.println("Margem Lucro: " + String.format(Locale.US, "%.2f%%", produto.getMargemLucro() * 100));
        }
    }

    static void listarTodosOsProdutos() {
        cabecalho();
        System.out.println("--- Lista de Produtos Cadastrados (" + quantosProdutos + " itens) ---");
        if (quantosProdutos == 0) {
            System.out.println("Nenhum produto cadastrado.");
            return;
        }
        System.out.println("--------------------------------------------------");
        for (int i = 0; i < quantosProdutos; i++) {
            if (produtosCadastrados[i] != null) {
        	    System.out.printf("Código: %d\n%s\n",
                                  produtosCadastrados[i].hashCode(),
                                  produtosCadastrados[i].toString().replace("\n", "\n  "));
                System.out.println("--------------------------------------------------");
            }
        }
    }

    public static void iniciarNovoPedido() {
        cabecalho();
         if (pedidoAtual != null) {
            System.out.println("Já existe um pedido em andamento (ID: " + pedidoAtual.getIdPedido() + ").");
            System.out.print("Deseja descartá-lo e iniciar um novo? (S/N): ");
            String resposta = teclado.nextLine().trim().toUpperCase();
            if (!resposta.equals("S")) {
                System.out.println("Continuando com o pedido atual.");
                return;
            } else {
                 System.out.println("Pedido ID " + pedidoAtual.getIdPedido() + " descartado.");
                 pedidoAtual = null;
            }
        }

        System.out.println("--- Iniciando Novo Pedido ---");
        int formaPagamento = -1;
        while (formaPagamento != 1 && formaPagamento != 2) {
             formaPagamento = lerInteiro("Forma de pagamento (1 - À Vista / 2 - Parcelado): ");
             if (formaPagamento != 1 && formaPagamento != 2) {
                 System.out.println("Opção inválida. Digite 1 ou 2.");
             }
        }

    	pedidoAtual = new Pedido(LocalDate.now(), formaPagamento);
    	System.out.println("Novo pedido (ID: " + pedidoAtual.getIdPedido() + ") iniciado.");
        System.out.println("Use a opção 5 para incluir produtos.");
    }

     public static void incluirProdutoNoPedido() {
        cabecalho();
        System.out.println("--- Incluir Produto no Pedido Atual ---");
        if (pedidoAtual == null) {
            System.out.println("Nenhum pedido iniciado. Use a opção 4 para iniciar.");
            return;
        }

        System.out.println("Pedido atual ID: " + pedidoAtual.getIdPedido() + " (" + pedidoAtual.getQuantosProdutos() + "/" + Pedido.MAX_PRODUTOS + " itens)");

        Produto produto = null;
        int opcaoBusca = lerInteiro("Buscar produto por (1-Código / 2-Nome)? ");

        if (opcaoBusca == 1) {
            produto = buscarProdutoPorCodigo();
        } else if (opcaoBusca == 2) {
             produto = buscarProdutoPorNome();
             if (produto == null) {
                  // A mensagem já foi dada em buscarProdutoPorNome
             }
        } else {
            System.out.println("Opção de busca inválida.");
        }


        if (produto != null) {
            if (produto instanceof ProdutoPerecivel) {
                if (((ProdutoPerecivel) produto).getDataDeValidade().isBefore(LocalDate.now())) {
                    System.out.println("Atenção: O produto '" + produto.getDescricao() + "' está VENCIDO e não pode ser adicionado ao pedido.");
                    return;
                }
            }

            if (pedidoAtual.incluirProduto(produto)) {
                 System.out.println("Produto '" + produto.getDescricao() + "' adicionado ao pedido.");
            } else {
                 // Mensagem já dada por incluirProduto
            }
        }

        System.out.println("\n--- Estado Atual do Pedido ID " + pedidoAtual.getIdPedido() + " ---");
        System.out.println(pedidoAtual);
    }


    public static void finalizarPedidoAtual() {
        cabecalho();
        System.out.println("--- Finalizar Pedido Atual ---");
    	if (pedidoAtual != null) {
            if (pedidoAtual.getQuantosProdutos() == 0) {
                 System.out.println("Pedido (ID: " + pedidoAtual.getIdPedido() + ") está vazio. Não pode ser finalizado.");
                 System.out.print("Deseja cancelar este pedido vazio? (S/N): ");
                 if (teclado.nextLine().trim().equalsIgnoreCase("S")) {
                     pedidoAtual = null;
                     System.out.println("Pedido cancelado.");
                 }
                 return;
            }

            System.out.println("--- Resumo do Pedido ---");
            System.out.println(pedidoAtual);
            System.out.print("\nConfirmar finalização do pedido ID " + pedidoAtual.getIdPedido() + "? (S/N): ");
            String confirma = teclado.nextLine().trim().toUpperCase();

            if (confirma.equals("S")) {
                filaPedidos.inserir(pedidoAtual);
                System.out.println("Pedido ID " + pedidoAtual.getIdPedido() + " finalizado e adicionado à fila.");
                pedidoAtual = null;
            } else {
                 System.out.println("Finalização cancelada. Pedido continua ativo.");
            }
        } else {
            System.out.println("Nenhum pedido ativo para finalizar. Use a opção 4 para iniciar.");
        }
    }

    public static void listarProdutosPedidosNaFila() {
        cabecalho();
        System.out.println("--- Listar Próximos Pedidos na Fila ---");

        if (filaPedidos.vazia()) {
            System.out.println("A fila de pedidos está vazia.");
            return;
        }

        int quantidade = lerInteiro("Quantos pedidos do início da fila deseja visualizar? ");
        if (quantidade <= 0) {
            System.out.println("Quantidade deve ser positiva.");
            return;
        }

        System.out.println("\nVisualizando os próximos " + quantidade + " pedido(s) na fila (sem removê-los):");

        Fila<Pedido> filaAux = new Fila<>();
        Pilha<Pedido> pilhaAux = new Pilha<>();
        int contador = 0;
        boolean erroLeitura = false;

        // 1. Move da fila principal para a pilha
        while(!filaPedidos.vazia()) {
            try {
                pilhaAux.empilhar(filaPedidos.remover());
            } catch (NoSuchElementException e) {
                 System.err.println("Erro ao mover da fila principal para pilha auxiliar.");
                 erroLeitura = true;
                 break;
            }
        }

        // 2. Move da pilha para a fila auxiliar e mostra os primeiros 'quantidade'
        while(!pilhaAux.vazia() && !erroLeitura) {
             try {
                Pedido pedidoDaVez = pilhaAux.desempilhar();
                if (contador < quantidade) {
                     System.out.println("\n------------ Pedido ID: " + pedidoDaVez.getIdPedido() + " ------------");
                     System.out.println(pedidoDaVez);
                     System.out.println("----------------------------------------");
                     contador++;
                }
                filaAux.inserir(pedidoDaVez); // Adiciona à fila auxiliar para restaurar
             } catch (NoSuchElementException e) {
                 System.err.println("Erro ao mover da pilha auxiliar para fila auxiliar.");
                 erroLeitura = true;
                 break;
             }
        }

        // 3. Move de volta da fila auxiliar para a fila principal (restaura a ordem original)
         while (!filaAux.vazia() && !erroLeitura) {
            try {
                filaPedidos.inserir(filaAux.remover());
            } catch (NoSuchElementException e) {
                System.err.println("Erro inesperado ao restaurar a fila de pedidos principal.");
                break;
            }
        }

        if (contador == 0 && !erroLeitura) {
             System.out.println("\nNenhum pedido para listar.");
        } else if (contador < quantidade && !erroLeitura) {
            System.out.println("\n(Fim da fila atingido. Mostrados " + contador + " pedido(s))");
        }
    }


	public static void main(String[] args) {

		teclado = new Scanner(System.in, StandardCharsets.UTF_8);
        produtosCadastrados = lerProdutos(nomeArquivoDados);

        if (quantosProdutos == 0 && !(new File(nomeArquivoDados)).exists()) {
             System.err.println("\n*******************************************************");
             System.err.println("ERRO CRÍTICO: Arquivo '" + nomeArquivoDados + "' não encontrado ou vazio.");
             System.err.println("O programa será encerrado.");
             System.err.println("*******************************************************");
             pausa();
             teclado.close();
             return;
        } else if (quantosProdutos == 0) {
            System.err.println("\n*******************************************************");
            System.err.println("AVISO: Nenhum produto foi carregado do arquivo '" + nomeArquivoDados + "'.");
            System.err.println("Verifique o conteúdo e o formato do arquivo.");
            System.err.println("*******************************************************");
            pausa();
        }


        int opcao = -1;
        do {
            try {
                opcao = menu();
                switch (opcao) {
                    case 1: listarTodosOsProdutos(); break;
                    case 2: mostrarProdutoEncontrado(buscarProdutoPorCodigo()); break;
                    case 3: buscarProdutoPorNome(); break;
                    case 4: iniciarNovoPedido(); break;
                    case 5: incluirProdutoNoPedido(); break;
                    case 6: finalizarPedidoAtual(); break;
                    case 7: listarProdutosPedidosNaFila(); break;
                    case 0: System.out.println("\nSaindo do sistema..."); break;
                    default: System.out.println("Opção inválida. Tente novamente."); break;
                }
            } catch (Exception e) {
                 System.err.println("\n!! Ocorreu um erro inesperado: " + e.getClass().getSimpleName() + " - " + e.getMessage());
                 System.err.println("Por favor, tente novamente.");
                 opcao = -1; // Garante que o loop continue
            }
            if (opcao != 0) {
                 pausa();
            }
        } while(opcao != 0);

        teclado.close();
        System.out.println("Sistema finalizado.");
    }
}

