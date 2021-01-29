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
    private transient Integer amountBoxesDead;
    private transient ArrayList<Attack> attacks;
    private transient Player owner;


    public void initPowers() {
        attacks = new ArrayList<>(3);
        for (IDATTACK idAttack : idAttacks) addPower(idAttack);
    }

    private void addPower(IDATTACK idAttack) {
        attacks.add(AttackFactory.getAttack(idAttack, this));
    }

    public boolean boxDied(){
        health = (health * amountBoxesDead) / amountBoxes;
        return health <= 0;
    }

    public void setAmountBoxes(Integer amountBoxes) {
        this.amountBoxes = amountBoxes;
        this.amountBoxesDead = amountBoxes;
    }

    public void addIdAttacks(ArrayList<Attack> attacks) {
        System.out.println("###########Se  van a agregar mas ataques ######## ");
        System.out.println("Se va a agregar: " + Arrays.toString(attacks.stream().map(attack -> attack.getClass().toString()).toArray()));
        System.out.println("A : " + Arrays.toString(this.attacks.stream().map(attack -> attack.getClass().toString()).toArray()));

        ArrayList<Attack> notRepeated = new ArrayList<>();

        for(Attack a: attacks) {
            notRepeated.add(a);
            for(Attack at: this.attacks){
                if(at.getClass() == a.getClass()) {
                    notRepeated.remove(a);
                    break;
                }
            }
        }

        this.attacks.addAll(notRepeated);
        System.out.println("Resultado : " + Arrays.toString(this.attacks.stream().map(attack -> attack.getClass().toString()).toArray()));

        System.out.println("########### Fin ######## ");
    }

    public void addIdAttacks(IDATTACK[] ids) {
        System.out.println("------ agregando mas ids de ataque-------------");
        System.out.println("A agregar : " + Arrays.toString(ids));
        System.out.println("En : " + Arrays.toString(idAttacks));

        ArrayList<IDATTACK> id = new ArrayList<>();
        id.addAll(Arrays.asList(idAttacks));
        for (int i = 0; i < ids.length; i++) {
            if(!id.contains(ids[i])){
                id.add(ids[i]);
            }
        }

        idAttacks = (IDATTACK[]) id.toArray();

        System.out.println("Resultado : " + Arrays.toString(idAttacks));

        System.out.println("------fin ---------------");
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
