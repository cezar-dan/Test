package ro.pub.cs.systems.eim.practical2test;

import android.graphics.Bitmap;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown=true)
public class PokemonInformation {
    private final ArrayList<String> types = new ArrayList<>();
    private final ArrayList<String> abilities = new ArrayList<>();
    private String image;

    @JsonIgnore
    private Bitmap bitmap;

    @SuppressWarnings("unchecked")
    @JsonProperty("types")
    private void unpackTypes(ArrayList<Map<String, Object>> types) {
        for (Map<String, Object> type : types) {
            this.types.add(((Map<String, String>)type.get("type")).get("name"));
        }
    }

    public ArrayList<String> getTypes() {
        return types;
    }

    @SuppressWarnings("unchecked")
    @JsonProperty("abilities")
    private void unpackAbilities(ArrayList<Map<String, Object>> abilities) {
        for (Map<String, Object> ability : abilities) {
            this.abilities.add(((Map<String, String>)ability.get("ability")).get("name"));
        }
    }

    public String getImage() {
        return image;
    }

    @JsonProperty("sprites")
    private void unpackImage(Map<String, Object> sprites) {
        this.image = (String) sprites.get("front_default");
    }

    public ArrayList<String> getAbilities() {
        return abilities;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
