package server.model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;

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

    public void initPowers(){
        attacks = new ArrayList<>(3);
        for (IDATTACK idAttack : idAttacks) addPower(idAttack);
    }

    private void addPower(IDATTACK idAttack) {
        attacks.add(AttackFactory.getAttack(idAttack, this));
    }

    @Override
    public String toString() {
        return "Champion{" +
                "name='" + name + '\'' +
                ", idAttacks=" + Arrays.toString(idAttacks) +
                ", percentage=" + percentage +
                ", power=" + power +
                ", resistance=" + resistance +
                ", health=" + health +
                ", amountBoxes=" + amountBoxes +
                '}';
    }
}
