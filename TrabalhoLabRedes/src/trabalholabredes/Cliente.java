/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalholabredes;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;

/**
 *
 * @author guile
 */
public class Cliente extends Thread{
    
    //Variaveis que permitem a identificação de um cliente
    
    int porta;
    String ip;
    String nome;
    
    //Dados do socket
    Socket cliente;
    DataInputStream din; //entradas
    DataOutputStream dout; //saidas
    
    
    String mensagem; //mensagem recebida pelo DIN
    
    //variaveis da interface gráfica
    DefaultListModel<String> model; //lista de items da interface gráfica
    JLabel situacao;
    ListaProdutos lp; //objeto contendo os produtos
    JList<String> lista; //jlist propriamente dito
    JButton comprar;
    JButton atualizar; //essas ultimas três variaveis so estão aqui para retirar o disable
    
    //controladores do update
    boolean updating;
    int sizeUpdate;
    
    
    //getters e setters
    
    public int getPorta() {
        return porta;
    }

    public void setPorta(int porta) {
        this.porta = porta;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Socket getCliente() {
        return cliente;
    }

    public void setCliente(Socket cliente) {
        this.cliente = cliente;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    
    //construtor que inicia o serviço
    
    public Cliente(int porta, String ip, String nome, JLabel situacao, DefaultListModel<String> model, JList lista, JButton comprar, JButton atualizar) {
        this.porta = porta;
        this.ip = ip;
        this.nome = nome;
        this.mensagem = "";
        this.situacao = situacao;
        this.model = model;
        this.lp = new ListaProdutos();
        this.sizeUpdate = 0;
        this.lista = lista;
        this.comprar = comprar;
        this.atualizar = atualizar;
        try{
            cliente = new Socket(ip, porta); //conexão começa aqui
            dout = new DataOutputStream(cliente.getOutputStream()); //instanciando saidas
            din = new DataInputStream(cliente.getInputStream()); //instanciando entradas
            dout.writeUTF("Nome do Cliente: " + nome + "\n"); //enviando nome do cliente conectado
            
        }catch(SocketException e){
            JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor");
        }
        
    }
    
    //thread responsavel por receber mensagens e processa-las
    
    @Override
    public void run() {
        while(!mensagem.equals("exit")){ //condicao de parada (infinita)
            try {
                mensagem = din.readUTF(); //recebe a mensagem 
                if(updating){ //update da lista
                    if(sizeUpdate == 0){
                        sizeUpdate = Integer.parseInt(mensagem);
                        lp = new ListaProdutos();
                    }
                    else{
                        // adiciona os items da lista de produtos ao modelo do Jlist
                        lp.getListaProdutos().add(lp.stringToProduto(mensagem));
                        sizeUpdate--;
                        if(sizeUpdate == 0){
                            updating = false;
                            model.removeAllElements();
                            for(int i = 0; i < lp.size(); i++){
                                model.add(i, lp.get(i).retornarProduto());
                            }
                            lista.setEnabled(true); //ativa os botoes e lista apos o update
                            comprar.setEnabled(true);
                            atualizar.setEnabled(true);
                        }
                    }
                }else{
                    if(mensagem.equals("send-list")){ //inicia a requisicao de update
                        updating = true;
                        lista.setEnabled(false); //desativa os botoes e lista dps do update
                        comprar.setEnabled(false);
                        atualizar.setEnabled(false);
                    }
                        if(mensagem.equals("Conexão com a loja bem sucedida!")) situacao.setText("Conectado a loja!");
                    }
            } catch (IOException ex) {
                Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
 
    //faz uma requisição de atualização para 
    public void atualizar() throws IOException{
        model.removeAllElements();
        dout.writeUTF("update");
        System.out.println("Iniciou um update");
    }
    
    
    //envia uma requisicao de compra e checa se o preco está dentro da carteira
    public double comprar(int pos, double valorCarteira) throws IOException{
        atualizar();
        if(valorCarteira < lp.get(pos).getPreco()) JOptionPane.showMessageDialog(null, "Você não possuí fundos suficientes!");
        else{
            if(lp.get(pos).getQuantidade() < 1) JOptionPane.showMessageDialog(null, "Esse produto não possuí unidades disponíveis!");
            else{
                dout.writeUTF("buy-"+pos+"-"+nome);
                return lp.get(pos).getPreco();
            }
        }
        return 0;
    }
    
}
