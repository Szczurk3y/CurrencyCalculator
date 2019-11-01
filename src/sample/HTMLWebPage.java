package sample;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.LinkedList;

public class HTMLWebPage {
    private String webPage_url;
    private Document document;
    private LinkedList<String> currenciesName = new LinkedList<>();
    private LinkedList<String> currenciesSymbol = new LinkedList<>();
    private LinkedList<String> currenciesValue = new LinkedList<>();

    public HTMLWebPage(String url) {
        this.webPage_url = url;
        init();
    }

    private void init() {
        try {
            this.document = Jsoup.connect(webPage_url).get();
            currenciesName.add("Polski złoty");
            currenciesSymbol.add("PLN");
            currenciesValue.add("1");
            for (Element row : document.select("div.rt-tbody div.rt-tr-group")) { // Table named rt-tbody iKAsXu in the website
                if (row.select("div.rt-td:nth-of-type(1)").text().equals("")) {
                    continue;
                } else {
                    currenciesName.add(row.select("div.rt-td:nth-of-type(1)").text());
                    currenciesSymbol.add(row.select("div.rt-td:nth-of-type(2)").text());
                    currenciesValue.add(row.select("div.rt-td:nth-of-type(3)").text());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void printWeb() {
        for (int i = 0; i < currenciesName.size(); i++) {
            System.out.println(currenciesName.get(i) + " " + currenciesSymbol.get(i) + " " + currenciesValue.get(i));
        }
    }

    public LinkedList<String> getCurrenciesNames() {
        return currenciesName;
    }

    public LinkedList<String> getCurrenciesSymbols() {
        return currenciesSymbol;
    }

    public LinkedList<String> getCurrenciesValues() {
        return currenciesValue;
    }
}
