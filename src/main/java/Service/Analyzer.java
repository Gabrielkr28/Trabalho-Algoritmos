/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Service;

import Entities.ListaEncadeada;
import Entities.NoLista;
import Entities.PilhaVetor;
import Entities.Tag;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Pichau
 */
public class Analyzer {
    private Pattern pattern;
    private Matcher matcher;
    
    private static final String tagFormat = "<(\"[^\"]*\"|'[^']*'|[^'\">])*>";
    private PilhaVetor<String> pilhaInicial = new PilhaVetor<String>(100);
    private PilhaVetor<String> pilhaFinal = new PilhaVetor<String>(100);
    ListaEncadeada<Tag> pilhaValidas = new ListaEncadeada<Tag>();
    ListaEncadeada<String> listaValidasString = new ListaEncadeada<String>();
    
    private final String[] singletons = {
        "meta",
        "base",
        "br",
        "col",
        "command",
        "embed",
        "hr",
        "img",
        "input",
        "link",
        "param",
        "source",
        "!DOCTYPE"};
    
    public void fileAnalyzer(String path) throws Exception{
        StringBuilder htmlFile = new StringBuilder();
        if (!path.endsWith(".html")){
            throw new Exception("Informe somente extensões .html!");
        }
        
        try{
            FileReader reader = new FileReader(path);
            BufferedReader br = new BufferedReader(reader);
            String tag;

            String tagString = "";
            
            pattern = Pattern.compile(tagFormat);
            
            while((tag=br.readLine()) != null){
                for(int i = 0; i < tag.length(); i++){
                    if(tag.charAt(i) == '<'){
                        if(!tagString.contains("<")){
                            tagString += tag.charAt(i);
                        }
                    }
                    else if(tag.charAt(i) == '>'){
                        if(!tagString.contains(">") && tagString.contains("<")){
                            tagString += tag.charAt(i);
                            
                            matcher = pattern.matcher(tagString);
                            boolean isValid = matcher.find();

                            if(isValid){
                                if(tagString.contains(" ")){
                                    var splitString = tagString.split(" ")[0] + ">";
                                    verificaTag(splitString);
                                }
                                else {
                                    verificaTag(tagString);
                                }
                            }
                            
                            tagString = "";
                        }
                    }
                    else {
                        if(tagString.contains("<")){
                          tagString += tag.charAt(i);  
                        }
                    }
                }
            }
            br.close();

            System.out.println("Inicial " + pilhaInicial.toString());
            pilhaInicial.inverterPilha();
            System.out.println("Inicial " + pilhaInicial.toString());
            System.out.println("Final  " + pilhaFinal.toString());
            System.out.println("Singletons  " + listaValidasString.toString());

            validaTags();

        }
        catch(Exception ex){
            System.out.println("Deu ruim" + ex.getMessage());
        }
    }
    
    private void validaTags(){
        while(!pilhaInicial.estaVazia()){
            String tagInicial = pilhaInicial.pop();
            String tagFinal = pilhaFinal.pop().split("/")[1];
            
            if(tagInicial.equals(tagFinal)){
                listaValidasString.inserir(tagInicial);
            }
        }
        
        int count = 0;
        
        for(int i = 0; i <= listaValidasString.obterComprimento(); i++){
            NoLista noAtual = listaValidasString.obterNo(i);
            
            for(int j = 0; j <= listaValidasString.obterComprimento(); j++){
                NoLista noCompara = listaValidasString.obterNo(j);
                
                if(noAtual == noCompara){
                    count++;
                    listaValidasString.retirar(noCompara.getInfo().toString());
                }
            }
            
            Tag newTag = new Tag(noAtual.getInfo().toString(), count);
            pilhaValidas.inserir(newTag);
            
            System.out.println("Tag1 = " + newTag.getName() + "  " + newTag.getCount());
            count = 0;
        }
    }
    
    private boolean singleton(String valor){
        for(int i = 0; i < singletons.length; i++){
            if(valor.equals(singletons[i])){
                return true;
            }
        }
        
        return false;
    }
    
    private void verificaTag(String tag){
        if(tag.contains("/")){
            tag = tag.split("<")[1].split(">")[0];

            if(singleton(tag)){
                listaValidasString.inserir(tag);
                return;
            }
            
            pilhaFinal.push(tag);
        }
        else {
            tag = tag.split("<")[1].split(">")[0];

            if(singleton(tag)){
                listaValidasString.inserir(tag);
                return;
            }
            
            pilhaInicial.push(tag);
        }
    }
}
