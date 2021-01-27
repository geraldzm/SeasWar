package server.model;

import lombok.Data;

import java.util.ArrayList;

@Data
public class Champion {
    private String name;
    private IDATTACK[] idAttacks;
    private Integer percentage;
    private Integer power;
    private Integer resistance;
    private Integer health;

    private transient Integer amountBoxes;
    private transient ArrayList<Attack> attacks;

    public void setPercentage(Integer percentage) {
        this.percentage = percentage;
        amountBoxes = 6 * percentage;
    }
}
