/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalholabredes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author guile
 */
public class ListaProdutos {
    
    ArrayList<Produto> listaProdutos;

    
    //getters e setters 
    
    public ArrayList<Produto> getListaProdutos() {
        return listaProdutos;
    }

    public void setListaProdutos(ArrayList<Produto> listaProdutos) {
        this.listaProdutos = listaProdutos;
    }

    
    //construtores
    
    public ListaProdutos(ArrayList<Produto> listaProdutos) {
        this.listaProdutos = listaProdutos;
    }
    
    public ListaProdutos() {
        listaProdutos = new ArrayList<>();
    }
    
    //funcoes para receber tamanho e elementos
    
    public int size(){
        return listaProdutos.size();
    }
    
    public Produto get(int i){
        return listaProdutos.get(i);
    }
    
    //conversao de valores pra strings ou objetos
    
    public String produtoToString(Produto p){
        return p.getId()+"~"+p.getNome()+"~"+p.getQuantidade()+"~"+p.getPreco();
    }
    
    public Produto stringToProduto(String s){
        String items[] = s.split("~");
        int id =  Integer.parseInt(items[0]);
        String nome = items[1];
        int quantidade = Integer.parseInt(items[2]);
        double preco = Double.parseDouble(items[3]);
        
        return new Produto(id, nome, quantidade, preco);
    }
    
    //salvar a lista num arquivo de texto
    
    public void salvar(){
        File f = new File("produtos.txt");
        if(!f.exists())try {
            f.createNewFile();
        } catch (IOException ex) {
            Logger.getLogger(ListaProdutos.class.getName()).log(Level.SEVERE, null, ex);
        }
        else{
            try {
                int tamanho = listaProdutos.size();
                FileWriter fw = new FileWriter(f);
                fw.write(""+ tamanho + "\r\n");
                for(int i = 0; i< tamanho; i++){
                    fw.write(""+ produtoToString(listaProdutos.get(i)));
                    if(tamanho != i + 1) fw.write("\r\n");
                }
                fw.close();
            } catch (IOException ex) {
                Logger.getLogger(ListaProdutos.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    //carrega os dados de um arquivo de texto
    
    public void carregar(){
         File f = new File("produtos.txt");
         listaProdutos = new ArrayList<>();
         if(f.exists()){
             try {
                 FileReader fr = new FileReader(f);
                 BufferedReader br = new BufferedReader(fr);
                 int tamanho = Integer.parseInt(br.readLine());
                 for(int i = 0; i < tamanho; i++){
                     Produto p;
                     p = stringToProduto(br.readLine());
                     listaProdutos.add(p);
                 }
             } catch (FileNotFoundException ex) {
                 Logger.getLogger(ListaProdutos.class.getName()).log(Level.SEVERE, null, ex);
             } catch (IOException ex) {
                 Logger.getLogger(ListaProdutos.class.getName()).log(Level.SEVERE, null, ex);
             }
         }else{
        
             try {
                 f.createNewFile();
                 FileWriter fw = new FileWriter(f);
                fw.write(""+ 0+ "\r\n");
             } catch (IOException ex) {
                 Logger.getLogger(ListaProdutos.class.getName()).log(Level.SEVERE, null, ex);
             }
             
         }
    }
    
    //cadastro de um novo produto na lista
    
    public void cadastrar(String nome, String quantidade, String preco){
        int id = 0;
        for(int i = 0; i < listaProdutos.size(); i++){
            if(id <= listaProdutos.get(i).getId()) id = listaProdutos.get(i).getId()+1;
        }
        int q = Integer.parseInt(quantidade);
        double p = Double.parseDouble(preco);
        
       listaProdutos.add(new Produto(id, nome, q, p));
        
    }
    
    //edição de um novo produto na lista
    
    public void editar(int pos, String nome, String quantidade, String preco){
        int q = Integer.parseInt(quantidade);
        double p = Double.parseDouble(preco);        
       listaProdutos.get(pos).setNome(nome);
       listaProdutos.get(pos).setPreco(p);
       listaProdutos.get(pos).setQuantidade(q);
        
    }
    
    //validação do produto, de inteiros e doubles
    
    public boolean validar(String nome, String quantidade, String preco){
        if(nome.isEmpty() || quantidade.isEmpty() || preco.isEmpty()) return false;
        if(!isNumber(quantidade) || !isDouble(preco)) return false;
        return true;
    }
    
    public boolean isNumber(String s){
        try{
            Integer.parseInt(s);
        }catch(NumberFormatException e){
            return false;
        }
        return true;
    }
    
    public boolean isDouble(String s){
        try{
            Double.parseDouble(s);
        }catch(NumberFormatException e){
            return false;
        }
        return true;
    }
    
    
    public void showAllElements(){
        for(int i = 0; i < listaProdutos.size();i++)System.out.println(listaProdutos.get(i).retornarProduto() + "\n");
    }
    
}
