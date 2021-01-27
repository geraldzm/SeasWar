package server.model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.ArrayList;

@Data
public class Champion {
    private String name;

    @SerializedName("attacks")
    private IDATTACK[] idAttacks;

    @SerializedName("per")
    private Integer percentage;
    private Integer power;

    @SerializedName("res")
    private Integer resistance;
    private Integer health;

    private transient Integer amountBoxes;
    private transient ArrayList<Attack> attacks;

    public void setPercentage(Integer percentage) {
        this.percentage = percentage;
        amountBoxes = 6 * percentage;
    }
}
