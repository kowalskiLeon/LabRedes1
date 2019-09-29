/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalholabredes;


import com.sun.java.swing.plaf.motif.MotifBorders;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.transformation.SortedList;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.plaf.basic.BasicDirectoryModel;
import javax.swing.plaf.metal.MetalBorders;

/**
 *
 * @author Guilherme Moura, Ricardo Sena
 */




public class Loja {
    
    static ListaProdutos lista;
    static Thread servidor;
    static ArrayList<String> mensagens;
    
    static JFrame janela;
    static JPanel jpanel;
    static JList listaProdutos;
    static JButton botaoCadastro;
    static JButton botaoEditar;
    static JButton botaoExcluir;
    static JTextArea areaMensagens;
    static JLabel portaTexto;
    static JTextField porta;
    static JButton botaoPorta;
    static JButton botaoAtualizar;
    static JButton botaoParar;
    static DefaultListModel<String> model;
    
    //variaveis responsaveis pela janela de cadastro/edição
    
    static int tipoForm; //0 - cadastrar, 1 - editar
    static int selectedEdition; //item selecionado para edição
    static JFrame janelaCrud;
    static JPanel jpanelCrud;
    static JLabel nomeProduto;
    static JLabel precoProduto;
    static JLabel quantidadeProduto;
    static JTextField nomeProdutoTexto;
    static JTextField precoProdutoTexto;
    static JTextField quantidadeProdutoTexto;
    static JButton cadastrarEditarBotao;
    static JButton cancelarBotao;
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
       inicializarInterface();
       lista = new ListaProdutos();
       atualizarLista();
       
       

    }
    
    //gera a interface gráfica para o programa
    
    public static void inicializarInterface(){
        //Instanciando a Janela
       janela = new JFrame("Sistema de Lojas - Servidor");    
       janela.setSize(640, 720);
       janela.setResizable(false);
       janela.setLocationRelativeTo(null); 
       janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       
       
       //Instanciando um JPanel
       jpanel = new JPanel();
       jpanel.setLayout(null);
       
       
       //Instanciando Lista de Produtos   
       model = new DefaultListModel<>();
       listaProdutos = new JList(model);
       listaProdutos.setBounds(30, 30, 400, 400);
       
       JScrollPane jsp = new JScrollPane();
       jsp.setViewportView(listaProdutos);
       jsp.setBounds(30, 30, 400, 400);
       jpanel.add(jsp);
       
       
       
       //Instanciando Botão de Cadastrar
       botaoCadastro = new JButton("Cadastrar");
       botaoCadastro.setBounds(470, 30, 100, 30);
       botaoCadastro.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
               abrirCadastro();
           }
       });
       jpanel.add(botaoCadastro);
       
       //Instanciando Botão de Editar
       botaoEditar = new JButton("Editar");
       botaoEditar.setBounds(470, 70, 100, 30);
       botaoEditar.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
               abrirEdicao();
           }
       });
       jpanel.add(botaoEditar);
       
       //Instanciando Botão de Remover
       botaoExcluir = new JButton("Remover");
       botaoExcluir.setBounds(470, 110, 100, 30);
       botaoExcluir.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
               remover();
           }
       });
       jpanel.add(botaoExcluir);

       
       //Instanciando area de mensagens
       areaMensagens = new JTextArea();
       areaMensagens.setBounds(30, 450, 580, 50);
       areaMensagens.setEditable(false);
       
       JScrollPane jspa = new JScrollPane();
       jspa.setViewportView(areaMensagens);
       jspa.setBounds(30, 450, 580, 200);
       jpanel.add(jspa);
       
       
       //Instanciando area para entrar com a porta
       portaTexto = new JLabel("Porta da conexão:");
       portaTexto.setBounds(470, 180, 120, 30);
       jpanel.add(portaTexto);
       
       porta = new JTextField();
       porta.setBounds(470, 210, 120, 30);
       jpanel.add(porta);
       
       botaoPorta = new JButton("Criar Porta");
       botaoPorta.setBounds(470, 250, 120, 30);
       botaoPorta.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
               criarConexao();
           }
       });
       
       jpanel.add(botaoPorta);
       
       //botao pra parar o servidor
       
       botaoParar = new JButton("Parar Conexão");
       botaoParar.setBounds(470, 290, 120, 30);
       botaoParar.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
                servidor.interrupt();
                botaoCadastro.setEnabled(true); //desativa os botoes de alterar os produtos após o começo do servidor
                botaoEditar.setEnabled(true);
                botaoExcluir.setEnabled(true);
                botaoParar.setEnabled(false);
           }
       });
       botaoParar.setEnabled(false);
       jpanel.add(botaoParar);
       
       botaoAtualizar = new JButton("Atualizar");
       botaoAtualizar.setBounds(470, 330, 120, 30);
       botaoAtualizar.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
                atualizarLista();
           }
       });
       
       jpanel.add(botaoAtualizar);
       
       //Jogando o JPanel pra Janela
       janela.add(jpanel);
       
       //tornando a janela visível
       janela.setVisible(true);
    }
   
    //atualiza a lista de produtos
    
    public static void atualizarLista(){
        lista.carregar();
        int tamanho = lista.size();
        model.removeAllElements();
        for(int i = 0; i < tamanho; i++){
           model.add(i, lista.get(i).retornarProduto());
        }
    }
    
    //cria a janela de cadasto-edição
    
    public static void criarJanela(){
        
        janelaCrud = new JFrame();
        if(tipoForm == 0)janelaCrud.setTitle("Cadastrar Produto");
        if(tipoForm == 1)janelaCrud.setTitle("Editar Produto");
        janelaCrud.setSize(320, 480);
        janelaCrud.setResizable(false);
        janelaCrud.setLocationRelativeTo(null); 
        janelaCrud.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        
        jpanelCrud = new JPanel();
        jpanelCrud.setLayout(null);
        
        
        nomeProduto = new JLabel("Nome do Produto");
        nomeProduto.setBounds(30, 20, 120, 30);
        jpanelCrud.add(nomeProduto);
        
        nomeProdutoTexto = new JTextField();
        nomeProdutoTexto.setBounds(30, 70, 200, 30);
        jpanelCrud.add(nomeProdutoTexto);
        
        precoProduto = new JLabel("Preço do Produto");
        precoProduto.setBounds(30, 120, 120, 30);
        jpanelCrud.add(precoProduto);
        
        precoProdutoTexto = new JTextField();
        precoProdutoTexto.setBounds(30, 170, 200, 30);
        jpanelCrud.add(precoProdutoTexto);
        
        quantidadeProduto = new JLabel("Quantidade do Produto");
        quantidadeProduto.setBounds(30, 220, 160, 30);
        jpanelCrud.add(quantidadeProduto);
        
        quantidadeProdutoTexto = new JTextField();
        quantidadeProdutoTexto.setBounds(30, 270, 120, 30);
        jpanelCrud.add(quantidadeProdutoTexto);

        cadastrarEditarBotao = new JButton();
        if(tipoForm == 0){
            cadastrarEditarBotao.setText("Cadastrar");
            cadastrarEditarBotao.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cadastrar();
                }
            });
        }
        if(tipoForm == 1){
            cadastrarEditarBotao.setText("Editar");
            cadastrarEditarBotao.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    editar();
                }
            });
        }
        cadastrarEditarBotao.setBounds(30, 320, 120, 40);
        jpanelCrud.add(cadastrarEditarBotao);
        
        cancelarBotao = new JButton("Cancelar");
        cancelarBotao.setBounds(180, 320, 120, 40);
        cancelarBotao.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                diposeWindow();
            }
        });
        jpanelCrud.add(cancelarBotao);
        
        janelaCrud.add(jpanelCrud);
        janelaCrud.setVisible(true);
    }
    
    //fecha a janela de cadastro - edição
    
    public static void diposeWindow(){
        janelaCrud.dispose();
    }
    
    //abre a subtela configurada para cadastro
    
    public static void abrirCadastro(){
        tipoForm = 0;
        criarJanela();
    }
    
    //abre a subtela configurada para edição
    
    public static void abrirEdicao(){
        tipoForm = 1;
        selectedEdition = listaProdutos.getSelectedIndex();
        if(selectedEdition >= 0 && selectedEdition <= Integer.MAX_VALUE){
            criarJanela();
            nomeProdutoTexto.setText(lista.get(selectedEdition).getNome());
            precoProdutoTexto.setText(lista.get(selectedEdition).getPreco() + "");
            quantidadeProdutoTexto.setText(lista.get(selectedEdition).getQuantidade()+"");
        }
        else JOptionPane.showMessageDialog(null, "Selecione um item para ser editado antes!");
    }
    
    //realiza o cadastro de um item
    
    public static void cadastrar(){
        if(lista.validar(nomeProdutoTexto.getText(), quantidadeProdutoTexto.getText(), precoProdutoTexto.getText())){
            lista.cadastrar(nomeProdutoTexto.getText(), quantidadeProdutoTexto.getText(), precoProdutoTexto.getText());
            lista.salvar();
            //lista.showAllElements();
            janelaCrud.dispose();
            areaMensagens.setText(areaMensagens.getText() + "Cadastrou: " + lista.get(lista.size()-1).retornarProduto()+"\n");
            atualizarLista();
        }else{
            JOptionPane.showMessageDialog(null, "Verifique os campos digitados. Algo deu errado!");
        }
    }
    
    //realiza o edição de um item
    
    public static void editar(){
        if(lista.validar(nomeProdutoTexto.getText(), quantidadeProdutoTexto.getText(), precoProdutoTexto.getText())){
            lista.editar(selectedEdition,nomeProdutoTexto.getText(), quantidadeProdutoTexto.getText(), precoProdutoTexto.getText());
            lista.salvar();
            //lista.showAllElements();
            janelaCrud.dispose();
            atualizarLista();
            areaMensagens.setText(areaMensagens.getText() + "Editou: " + lista.get(selectedEdition).retornarProduto()+"\n");
        }else{
            JOptionPane.showMessageDialog(null, "Verifique os campos digitados. Algo deu errado!");
        }
    }
    
    //remove um item
    
    public static void remover(){
            selectedEdition = listaProdutos.getSelectedIndex();
            if(selectedEdition >= 0 && selectedEdition <= Integer.MAX_VALUE){
                areaMensagens.setText(areaMensagens.getText() + "Removeu: " + lista.get(selectedEdition).retornarProduto()+"\n");
                lista.getListaProdutos().remove(selectedEdition);
                lista.salvar();
                atualizarLista();
                
            }
            else JOptionPane.showMessageDialog(null, "Selecione um item para ser editado antes!");
        }
    
    //cria a porta 
    
    public static void criarConexao(){
        int port = 0;
        try{
            port  = Integer.parseInt(porta.getText());
            servidor = new Servidor(port, areaMensagens, model);
            servidor.start();
            botaoCadastro.setEnabled(false); //desativa os botoes de alterar os produtos após o começo do servidor
            botaoEditar.setEnabled(false);
            botaoExcluir.setEnabled(false);
            botaoParar.setEnabled(true);
        }catch(NumberFormatException e){
            JOptionPane.showMessageDialog(null, "Porta invalida.");
        }
        
    }
    
}
