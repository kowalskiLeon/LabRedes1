/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalholabredes;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

/**
 *
 * @author Guilherme Moura, Ricardo Sena
 */

public class Servidor extends Thread{

    ServerSocket servidor;
    ArrayList<Socket> clientes;
    JTextArea areaMensagens;
    ArrayList<DataInputStream> din;
    ArrayList<DataOutputStream> dout;
    DefaultListModel<String> model;
    ListaProdutos lp;
    String mensagem;
    int currentClient;
    
    
        //Area da Classe que fica executando continuamente
    @Override
        public void run() {
            while(true){
            System.out.println("Running");
                try {
                    Socket c = servidor.accept(); //aceita conexao
                    clientes.add(c); //adiciona a lista de clientes
                    areaMensagens.setText(areaMensagens.getText() +"Cliente entrou na rede pelo ip: " + c.getInetAddress() + "\n"); //informa a conexão na area de mensagens
                    
                    //envia mensagem de confirmação da conexão
                    
                    
                    
                    
                    //começa uma thread nova para receber as mensagens
                    
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            int index = currentClient - 1;
                            try {
                                System.out.println("index atual:" + index);
                                din.add(new DataInputStream(c.getInputStream()));
                                dout.add(new DataOutputStream(c.getOutputStream()));
                                dout.get(index).writeUTF("Conexão com a loja bem sucedida!");
                                updateList(index);
                            } catch (IOException ex) {
                                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                            }
                                while(!mensagem.equals("exit")){
                                    try {
                                        mensagem = din.get(index).readUTF();
                                        if(mensagem.equals("update")) updateList(index);
                                        if(mensagem.contains("buy-")){
                                            System.out.println("Compra recebida");
                                            String nome = mensagem.split("-")[2];
                                            int i = Integer.parseInt(mensagem.split("-")[1]);
                                            areaMensagens.setText(areaMensagens.getText() + nome + " comprou " + lp.get(i).getNome() + "\n"); 
                                            lp.get(i).setQuantidade(lp.get(i).getQuantidade()-1);
                                            lp.salvar();
                                            model.removeAllElements();
                                            lp.carregar();
                                            for(int a  = 0; a < lp.size(); a++){
                                                model.add(a, lp.get(a).retornarProduto());
                                            }
                                        }
                                        if(mensagem.contains("Nome do Cliente")){
                                            areaMensagens.setText(areaMensagens.getText() + mensagem); 
                                        }
                                        
                                    } catch (IOException ex) {
                                        Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                            }
                                
                            
                        }
                        
                    }).start(); //fim da thread nova
                    currentClient++;
                } catch (IOException ex) {
                    Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    
        //atualiza a lista
    public void updateList(int index) throws IOException{
        dout.get(index).writeUTF("send-list"); //envia mensagem para o cliente dizendo que vai fazer um update da lista
        lp.carregar();
        dout.get(index).writeUTF(lp.size()+"");
        for(int i = 0; i < lp.size();i++){
            Produto p = lp.get(i);
            dout.get(index).writeUTF(lp.produtoToString(p));
        }
    }

    public ArrayList<Socket> getClientes() {
        return clientes;
    }

    public void setClientes(ArrayList<Socket> clientes) {
        this.clientes = clientes;
    }

    public ServerSocket getServidor() {
        return servidor;
    }

    public void setServidor(ServerSocket servidor) {
        this.servidor = servidor;
    }

    public JTextArea getAreaMensagens() {
        return areaMensagens;
    }

    public void setAreaMensagens(JTextArea areaMensagens) {
        this.areaMensagens = areaMensagens;
    }

    public ArrayList<DataInputStream> getDin() {
        return din;
    }

    public void setDin(ArrayList<DataInputStream> din) {
        this.din = din;
    }

    public ArrayList<DataOutputStream> getDout() {
        return dout;
    }

    public void setDout(ArrayList<DataOutputStream> dout) {
        this.dout = dout;
    }

    public DefaultListModel<String> getModel() {
        return model;
    }

    public void setModel(DefaultListModel<String> model) {
        this.model = model;
    }

    
    
    
    //construtor da classe
    
    public Servidor(int porta, JTextArea m, DefaultListModel<String> model) {
        try {
            servidor = new ServerSocket(porta);
            areaMensagens = m;
            areaMensagens.setText(areaMensagens.getText() +"Conexão na porta " + porta + " criada. Esperando clientes. \n");
            areaMensagens.setText(areaMensagens.getText() +"Seu IP:  " + InetAddress.getLocalHost()+ "\n");
            clientes = new ArrayList<>();
            mensagem = "";
            this.model = model;
            lp = new ListaProdutos();
            lp.carregar();
            dout = new ArrayList<>();
            din = new ArrayList<>();
            currentClient = 0;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Erro ao criar conexão. Verifique a porta.");
        }
        
    }
    
    public void parar(){
        try {
            for(int i = 0; i < clientes.size(); i++){
                clientes.get(i).close();
                din.get(i).close();
                dout.get(i).close();
            }
            servidor.close();
        } catch (IOException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void interrupt() {
        parar();
        super.interrupt(); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
    
    
    
}
