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
    Tag[] tagsCorretas = new Tag[100];
    Tag[] tagsIncoretas = new Tag[100];

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

    public void fileAnalyzer(String path) throws Exception {
        StringBuilder htmlFile = new StringBuilder();
        if (!path.endsWith(".html")) {
            throw new Exception("Informe somente extensões .html!");
        }

        try {
            FileReader reader = new FileReader(path);
            BufferedReader br = new BufferedReader(reader);
            String tag;

            String tagString = "";

            pattern = Pattern.compile(tagFormat);

            while ((tag = br.readLine()) != null) {
                for (int i = 0; i < tag.length(); i++) {
                    if (tag.charAt(i) == '<') {
                        if (!tagString.contains("<")) {
                            tagString += tag.charAt(i);
                        }
                    } else if (tag.charAt(i) == '>') {
                        if (!tagString.contains(">") && tagString.contains("<")) {
                            tagString += tag.charAt(i);

                            matcher = pattern.matcher(tagString);
                            boolean isValid = matcher.find();

                            if (isValid) {
                                if (tagString.contains(" ")) {
                                    var splitString = tagString.split(" ")[0] + ">";
                                    verificaTag(splitString);
                                } else {
                                    verificaTag(tagString);
                                }
                            }

                            tagString = "";
                        }
                    } else {
                        if (tagString.contains("<")) {
                            tagString += tag.charAt(i);
                        }
                    }
                }
            }
            br.close();

            System.out.println("Inicial " + pilhaInicial.toString());
//            pilhaInicial.inverterPilha();
            System.out.println("Inicial " + pilhaInicial.toString());
            System.out.println("Final  " + pilhaFinal.toString());
            System.out.println("Singletons  " + listaValidasString.toString());

            System.out.println("---------------------------");
            
            Tag[] c = contaTags(convertePilhaParaVetor(pilhaInicial));
            repopularPilha(c, pilhaInicial);
            for (Tag a : c) {
                if (a != null) {
                    System.out.println(a.getName() + "\n" + a.getCount());
                }

            }
            Tag[] x = contaTags(convertePilhaParaVetor(pilhaFinal));
            repopularPilha(x, pilhaFinal);
            for (Tag a : x) {
                if (a != null) {
                    System.out.println(a.getName() + "\n" + a.getCount());
                }

            }

            for (Tag a : contaTags(converteEncadeadaVetor(listaValidasString))) {
                if (a != null) {
                    System.out.println(a.getName() + "\n" + a.getCount());
                }

            }
            
            
            Tag[] f = contaTags(convertePilhaParaVetor(pilhaFinal));
            Tag[] i = contaTags(convertePilhaParaVetor(pilhaInicial));
            
            validaTagsIniciais(i, f);
            validaTagsFinais(i, f);
            
            System.out.println("Corretas ------");
            
            for(Tag a : tagsCorretas){
                if (a != null) {
                    System.out.println(a.getName() + "\n" + a.getCount());
                }
            }
            
            System.out.println("Incorretas ----");
            
            for(Tag a : tagsIncoretas){
                if (a != null) {
                    System.out.println(a.getName() + "\n" + a.getCount());
                }
            }

//            validaTags();
        } catch (Exception ex) {
            System.out.println("Deu ruim" + ex.getMessage());
            throw new Exception(ex);
        }
    }

//    private void validaTags(){
//        while(!pilhaInicial.estaVazia()){
//            String tagInicial = pilhaInicial.pop();
//            String tagFinal = pilhaFinal.pop().split("/")[1];
//            
//            if(tagInicial.equals(tagFinal)){
//                listaValidasString.inserir(tagInicial);
//            }
//        }
//        
//        int count = 0;
//        
//        for(int i = 0; i <= listaValidasString.obterComprimento(); i++){
//            NoLista noAtual = listaValidasString.obterNo(i);
//            
//            for(int j = 0; j <= listaValidasString.obterComprimento(); j++){
//                NoLista noCompara = listaValidasString.obterNo(j);
//                
//                if(noAtual.getInfo().equals(noCompara.getInfo())){
//                    count++;
//                    listaValidasString.retirar(noCompara.getInfo().toString());
//                }
//            }
//            
//            Tag newTag = new Tag(noAtual.getInfo().toString(), count);
//            pilhaValidas.inserir(newTag);
//            
//            System.out.println("Tag1 = " + newTag.getName() + "  " + newTag.getCount());
//            count = 0;
//        }
//    }
    private boolean singleton(String valor) {
        for (int i = 0; i < singletons.length; i++) {
            if (valor.equals(singletons[i])) {
                return true;
            }
        }

        return false;
    }

    private void verificaTag(String tag) {
        if (tag.contains("/")) {
            tag = tag.split("<")[1].split(">")[0];

            if (singleton(tag)) {
                listaValidasString.inserir(tag);
                return;
            }

            pilhaFinal.push(tag);
        } else {
            tag = tag.split("<")[1].split(">")[0];

            if (singleton(tag)) {
                listaValidasString.inserir(tag);
                return;
            }

            pilhaInicial.push(tag);
        }
    }

    public Tag[] contaTags(String[] tags) {
        Tag[] vetorTags = new Tag[tags.length];
        for (int i = 0; i < tags.length; i++) {
            int posicaoTag = pesquisaTagNome(tags[i], vetorTags);
            if (posicaoTag == -1) {
                Tag tag = new Tag(tags[i], 1);
                vetorTags[i] = tag;
            } else {
                Tag tag = vetorTags[posicaoTag];
                tag.setCount(tag.getCount() + 1);
                vetorTags[posicaoTag] = tag;
            }
        }
        return vetorTags;
    }

    private String[] convertePilhaParaVetor(PilhaVetor<String> pilha) {
        String[] stringTags = new String[pilha.size()];
        PilhaVetor<String> pilhaVetor = pilha;
        int size = pilhaVetor.size();
        for (int i = 0; i < size; i++) {
            String tagName = pilhaVetor.pop();
            if (tagName != null) {
                stringTags[i] = tagName;
            }

        }

        return stringTags;
    }

    private String[] converteEncadeadaVetor(ListaEncadeada<String> lista) {
        String[] stringTags = new String[lista.obterComprimento()];
        ListaEncadeada<String> listaEncadeada = lista;
        int size = lista.obterComprimento();
        for (int i = 0; i < size; i++) {
            String tagName = listaEncadeada.obterNo(i).getInfo();
            if (tagName != null) {
                stringTags[i] = tagName;
            }

        }

        return stringTags;
    }

    private int pesquisaTagNome(String valor, Tag[] tags) {
        for (int i = 0; i < tags.length; i++) {
            if (tags[i] != null) {
                if (tags[i].getName().equals(valor)) {
                    return i;
                }
            }
        }
        return -1;
    }

    private void validaTagsIniciais(Tag[] iniciais, Tag[] finais) {
        Tag[] newIniciais = iniciais;
        Tag[] newFinais = finais;
        boolean control = false;
        for (int i = 0; i < newIniciais.length; i++) {
            if (newIniciais[i] != null) {
                for (int j = 0; j < newFinais.length; j++) {
                    if (newFinais[j] != null) {
                        if (newIniciais[i].getName().equals(newFinais[j].getName().replace("/", ""))) {
                            if (pesquisaTagNome(newIniciais[i].getName(), tagsCorretas) == -1) {
                                tagsCorretas[i] = newIniciais[i];
                                control = true;
                                newFinais[j] = null;
                                break;
                            }
                        } else {
                            control = false;
                        }
                    }
                }
                if (!control) {
                    tagsIncoretas[i] = newIniciais[i];
                }
            }
        }
    }

    private void validaTagsFinais(Tag[] iniciais, Tag[] finais) {
        Tag[] newIniciais = iniciais;
        Tag[] newFinais = finais;
        boolean control = false;
        for (int i = 0; i < newFinais.length; i++) {
            if (newFinais[i] != null) {
                for (int j = 0; j < newIniciais.length; j++) {
                    if (newIniciais[j] != null) { 
                        if (newFinais[i].getName().replace("/", "").equals(newIniciais[j].getName())) {
                            if (pesquisaTagNome(newFinais[i].getName(), tagsCorretas) == -1) {
                                tagsCorretas[i] = newFinais[i];
                                control = true;
                                newIniciais[j] = null;
                                break;
                            }
                        } else {
                            control = false;
                        }
                    }
                }
                if (!control) {
                    tagsIncoretas[i] = newFinais[i];
                }
            }
        }
    }
    
    private void repopularPilha(Tag[] vetor, PilhaVetor<String> pilha){
        for(Tag tag : vetor){
            if(tag != null){
                pilha.push(tag.getName());
            }
        }
    }

}
