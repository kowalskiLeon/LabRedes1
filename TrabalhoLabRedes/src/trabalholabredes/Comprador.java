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
import java.io.IOException;
import java.util.AbstractList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.transformation.SortedList;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.plaf.basic.BasicDirectoryModel;
import javax.swing.plaf.metal.MetalBorders;
import static trabalholabredes.Loja.listaProdutos;

/**
 *
 * @author Guilherme Moura, Ricardo Sena
 */




public class Comprador {
    
    static Cliente cliente;
    
    static JFrame janela;
    static JPanel jpanel;
    static JLabel ipLabel;
    static JLabel portLabel;
    static JLabel nomeLabel;
    static JTextField ipEntrada;
    static JTextField portEntrada;
    static JTextField nomeEntrada;
    static JButton botaoEntrar;
    static JLabel situacaoLabel;
    static JList<String> listaProdutos;
    static JButton botaoComprar;
    static JButton botaoAtualizar;
    static JLabel carteiraLabel;
    static JLabel valorCarteira;
    static JButton botaoAdicionarFundos;
    static DefaultListModel<String> model;
    static double preco;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
       inicializarInterface();
       
    }
    
    public static void inicializarInterface(){
        //Instanciando a Janela
       
        preco = 0;
       
       janela = new JFrame("Sistema de Lojas - Cliente");    
       janela.setSize(640, 720);
       janela.setResizable(false);
       janela.setLocationRelativeTo(null); 
       janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       
       
       //Instanciando um JPanel
       jpanel = new JPanel();
       jpanel.setLayout(null);
       
       ipLabel = new JLabel("Ip:");
       ipLabel.setBounds(30, 30, 120, 30);
       jpanel.add(ipLabel);
       
       portLabel = new JLabel("Porta:");
       portLabel.setBounds(180, 30, 120, 30);
       jpanel.add(portLabel);
       
       portLabel = new JLabel("Nome:");
       portLabel.setBounds(330, 30, 120, 30);
       jpanel.add(portLabel);
       
       ipEntrada = new JTextField();
       ipEntrada.setBounds(30, 60, 120, 30);
       jpanel.add(ipEntrada);
       
       portEntrada = new JTextField();
       portEntrada.setBounds(180, 60, 120, 30);
       jpanel.add(portEntrada);
       
       nomeEntrada = new JTextField();
       nomeEntrada.setBounds(330, 60, 120, 30);
       jpanel.add(nomeEntrada);
       
       botaoEntrar = new JButton("Conectar");
       botaoEntrar.setBounds(480, 60, 120, 30);
       botaoEntrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                conectar();
            }
        });
       jpanel.add(botaoEntrar);
       
       //Label dizendo sobre a situação da conexão
       situacaoLabel = new JLabel("Nenhuma conexão foi feita ainda.");
       situacaoLabel.setBounds(30, 90, 600, 30);
       jpanel.add(situacaoLabel);
       
       //Lista de produtos (inicializa desativado)
       model = new DefaultListModel<>();
       listaProdutos = new JList(model);
       listaProdutos.setBounds(30, 180, 300, 400);
       
       JScrollPane jsp = new JScrollPane();
       jsp.setViewportView(listaProdutos);
       jsp.setBounds(30, 180, 300, 400);
       jpanel.add(jsp);
       listaProdutos.setEnabled(false);
       
       //Botao de Compra (inicializa desativado)
       botaoComprar = new JButton("Comprar");
       botaoComprar.setBounds(30, 600, 120, 30);
       botaoComprar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(listaProdutos.getSelectedIndex() < 0 || listaProdutos.getSelectedIndex() > Integer.MAX_VALUE)
                    JOptionPane.showMessageDialog(null, "Selecione um item primeiro!");
                else
                try {
                    preco -= cliente.comprar(listaProdutos.getSelectedIndex(), preco);
                    valorCarteira.setText("R$" + preco);
                    cliente.atualizar();
                } catch (IOException ex) {
                    Logger.getLogger(Comprador.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
       jpanel.add(botaoComprar);
       botaoComprar.setEnabled(false);
       
       //Botao de Atualização (inicializa desativado)
       botaoAtualizar = new JButton("Atualizar");
       botaoAtualizar.setBounds(180, 600, 120, 30);
       botaoAtualizar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               try {
                    cliente.atualizar();
                } catch (IOException ex) {
                    Logger.getLogger(Comprador.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
       jpanel.add(botaoAtualizar);
       botaoAtualizar.setEnabled(false);
       
       //Dados da Carteira
       carteiraLabel = new JLabel("Saldo da Carteira");
       carteiraLabel.setBounds(400, 200, 120, 30);
       jpanel.add(carteiraLabel);
       
       valorCarteira = new JLabel("R$" + preco);
       valorCarteira.setBounds(400, 230, 120, 50);
       
       jpanel.add(valorCarteira);
       
       botaoAdicionarFundos = new JButton("Adicionar Fundos");
       botaoAdicionarFundos.setBounds(400, 300, 150, 30);
       botaoAdicionarFundos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                adicionarFundos();
            }
        });
       jpanel.add(botaoAdicionarFundos);
       
       //Jogando o JPanel pra Janela
       janela.add(jpanel);
       
       //tornando a janela visível
       janela.setVisible(true);
    }
    
    public static void adicionarFundos(){
        String r = JOptionPane.showInputDialog("Insira um valor inteiro");
        System.out.println(r);
        double add = 0;
        try{
            add = Double.parseDouble(r);
            preco += add;
            valorCarteira.setText("R$" + preco);
            
        }catch(NumberFormatException e){
            JOptionPane.showMessageDialog(null, "O valor não é válido. Tente novamente");
        }
        
    }
    
    public static void conectar(){
        
        int porta = Integer.parseInt(portEntrada.getText());
        cliente = new Cliente(porta, ipEntrada.getText(), nomeEntrada.getText(), situacaoLabel, model, listaProdutos , botaoComprar, botaoAtualizar);
        cliente.start();
    }
   
    
}
