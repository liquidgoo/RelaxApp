package by.bsuir.relaxapp;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.InputStream;

public class Sign {
    public String name;
    public String dateRange;
    public String description;
    public String compatibility;
    public String mood;
    public String color;
    public String luckyNumber;
    public String luckyTime;

    Sign(String name,InputStream stream) {
        this.name=name;
        parse(stream);
    }

    public void parse(InputStream stream) {
        try {
            JsonFactory jfactory = new JsonFactory();
            JsonParser jParser = jfactory.createParser(stream);
            while (jParser.nextToken() != JsonToken.END_OBJECT) {
                if (jParser.getCurrentName()==null){
                    continue;
                }
                switch (jParser.getCurrentName()) {
                    case "date_range":
                        jParser.nextToken();
                        dateRange = jParser.getText();
                        break;
                    case "description":
                        jParser.nextToken();
                        description = jParser.getText();
                        break;
                    case "compatibility":
                        jParser.nextToken();
                        compatibility = jParser.getText();
                        break;
                    case "mood":
                        jParser.nextToken();
                        mood = jParser.getText();
                        break;
                    case "color":
                        jParser.nextToken();
                        color = jParser.getText();
                        break;
                    case "lucky_number":
                        jParser.nextToken();
                        luckyNumber = jParser.getText();
                        break;
                    case "lucky_time":
                        jParser.nextToken();
                        luckyTime = jParser.getText();
                        break;
                }

            }
            jParser.close();
        }catch (Exception e){
            e.printStackTrace(System.err);
        }
    }

    @Override
    public String toString() {
        return "Sign{" +
                "name='" + name + '\'' +
                ", dateRange='" + dateRange + '\'' +
                ", description='" + description + '\'' +
                ", compatibility='" + compatibility + '\'' +
                ", mood='" + mood + '\'' +
                ", color='" + color + '\'' +
                ", luckyNumber='" + luckyNumber + '\'' +
                ", luckyTime='" + luckyTime + '\'' +
                '}';
    }
}
