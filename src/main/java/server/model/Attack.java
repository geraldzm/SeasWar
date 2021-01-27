package server.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public abstract class Attack {

    private IDATTACK idattack;
    private Champion owner;

    abstract void firstAttack(Village village);
    abstract void secondAttack(Village village);
    abstract void thirdAttack(Village village);

}