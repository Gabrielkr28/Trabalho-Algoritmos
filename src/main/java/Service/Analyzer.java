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

    private static Pattern pattern;
    private static Matcher matcher;

    private static final String tagFormat = "<(\"[^\"]*\"|'[^']*'|[^'\">])*>";
    static private PilhaVetor<String> pilhaInicial = new PilhaVetor<String>(100);
    static private PilhaVetor<String> pilhaFinal = new PilhaVetor<String>(100);
    static ListaEncadeada<Tag> pilhaValidas = new ListaEncadeada<Tag>();
    static ListaEncadeada<String> listaValidasString = new ListaEncadeada<String>();
    static Tag[] tagsCorretasFinais = new Tag[100];
    static Tag[] tagsIncoretasFinais = new Tag[100];
    static Tag[] tagsCorretasIniciais = new Tag[100];
    static Tag[] tagsIncoretasIniciais = new Tag[100];

    public static Tag[] getTagsIncorretasFinais() {
        return tagsIncoretasFinais;
    }
    
    public static Tag[] getTagsIncorretasInciais() {
        return tagsIncoretasIniciais;
    }
    
    private static final String[] singletons = {
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
        "!DOCTYPE"
    };

    public static void fileAnalyzer(String path) throws Exception {
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

            Tag[] f = converteVetorStringToTag(convertePilhaParaVetor(pilhaFinal));
            Tag[] i = converteVetorStringToTag(convertePilhaParaVetor(pilhaInicial));

            repopularPilha(f, pilhaFinal);
            repopularPilha(i, pilhaInicial);

            Tag[] f1 = converteVetorStringToTag(convertePilhaParaVetor(pilhaFinal));
            Tag[] i1 = converteVetorStringToTag(convertePilhaParaVetor(pilhaInicial));

            validaTagsIniciais(i, f);
            validaTagsFinais(i1, f1);
            
//            validaTags();
        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
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
    private static boolean singleton(String valor) {
        for (int i = 0; i < singletons.length; i++) {
            if (valor.equals(singletons[i])) {
                return true;
            }
        }

        return false;
    }

    private static void verificaTag(String tag) {
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

    public static Tag[] converteVetorStringToTag(String[] tags) {
        Tag[] vetorTags = new Tag[tags.length];
        for (int i = 0; i < tags.length; i++) {
            Tag tag = new Tag(tags[i], 1);
            vetorTags[i] = tag;
        }
        return vetorTags;
    }

    private static String[] convertePilhaParaVetor(PilhaVetor<String> pilha) {
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

    public static Tag[] contarTags() {
        Tag[] tags = new Tag[tagsCorretasIniciais.length];
        for (int i = 0; i < tagsCorretasIniciais.length; i++) {
            Tag tagCorreta = tagsCorretasIniciais[i];
            if (tagCorreta != null) {
                int posicaoTag = pesquisaTagPorNome(tagCorreta.getName(), tags);
                if (posicaoTag == -1) {
                    tags[i] = tagCorreta;
                } else {
                    Tag tagExistente = tags[posicaoTag];
                    tagExistente.setCount(tagExistente.getCount() + 1);
                    tags[posicaoTag] = tagExistente;
                }
            }
        }
        return tags;
    }
    
    public static Tag[] contarTagsSingletons() {
        Tag[] tagsSingleton = converteListaEncadeadaParaTag(listaValidasString);
        Tag[] tags = new Tag[tagsSingleton.length];
        for (int i = 0; i < tags.length; i++) {
            Tag tagSingleton = tagsSingleton[i];
            if (tagSingleton != null) {
                int posicaoTag = pesquisaTagPorNome(tagSingleton.getName(), tags);
                if (posicaoTag == -1) {
                    tags[i] = tagSingleton;
                } else {
                    Tag tagExistente = tags[posicaoTag];
                    tagExistente.setCount(tagExistente.getCount() + 1);
                    tags[posicaoTag] = tagExistente;
                }
            }
        }
        return tags;
    }
    
    private static Tag[] converteListaEncadeadaParaTag(ListaEncadeada<String> listaEncadeada) {
        Tag[] tags = new Tag[listaEncadeada.obterComprimento()];
        for (int i = 0; i < tags.length; i++) {
            tags[i] = new Tag(listaEncadeada.obterNo(i).getInfo(), 1);
        }
        return tags;
    }
        

    private static int pesquisaTagPorNome(String nome, Tag[] tags) {
        for (int i = 0; i < tags.length; i++) {
            Tag tag = tags[i];
            if (tag != null) {
                if (tag.getName().equals(nome)) {
                    return i;
                }
            }
        }
        return -1;
    }

    private static void validaTagsIniciais(Tag[] iniciais, Tag[] finais) {
        Tag[] newIniciais = iniciais;
        Tag[] newFinais = finais;
        boolean control = false;
        for (int i = 0; i < newIniciais.length; i++) {
            for (int j = 0; j < newFinais.length; j++) {
                if (newFinais[j] != null) {
                    if (newIniciais[i].getName().equals(newFinais[j].getName().replace("/", ""))) {
                        tagsCorretasIniciais[i] = newIniciais[i];
                        control = true;
                        newFinais[j] = null;
                        break;
                    } else {
                        control = false;
                    }
                } else {
                    control = false;
                }
            }
            if (!control) {
                tagsIncoretasIniciais[i] = newIniciais[i];
            }
        }
    }

    private static void validaTagsFinais(Tag[] iniciais, Tag[] finais) {
        Tag[] newIniciais = iniciais;
        Tag[] newFinais = finais;
        boolean control = false;
        for (int i = 0; i < newFinais.length; i++) {
            for (int j = 0; j < newIniciais.length; j++) {
                if (newIniciais[j] != null) {
                    if (newFinais[i].getName().replace("/", "").equals(newIniciais[j].getName())) {
                        tagsCorretasFinais[i] = newFinais[i];
                        control = true;
                        newIniciais[j] = null;
                        break;
                    } else {
                        control = false;
                    }
                } else {
                    control = false;
                }
            }
            if (!control) {
                tagsIncoretasFinais[i] = newFinais[i];
            }
        }
    }

    private static void repopularPilha(Tag[] vetor, PilhaVetor<String> pilha) {
        for (Tag tag : vetor) {
            if (tag != null) {
                pilha.push(tag.getName());
            }
        }
    }
}
