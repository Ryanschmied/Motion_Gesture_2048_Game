package ca.uwaterloo.ece155_nlab4;

//Design FSM to reduce noise and ensure readings are what are intended
public class finiteStateMachine {

    float previousReading = 0;
    float currentReading = 0;
    float THRES_A1;
    float THRES_A2;
    float THRES_A3;
    float THRES_B1;
    float THRES_B2;
    float THRES_B3;
    float deltaAccel = 0;
    int sampleCount = 0;


    public enum StateType {
        RISE_A,
        RISE_B,
        WAIT,
        FALL_A,
        FALL_B,
        DETERMINED
    };

    public enum InputType{
        TYPE_A,
        TYPE_B,
        TYPE_X,
        UNDETERMINED
    }

    StateType state;
    StateType prevState;
    InputType type;

    public finiteStateMachine(float [] THRES){
        THRES_A1 = THRES[0];
        THRES_A2 = THRES[1];
        THRES_A3 = THRES[2];
        THRES_B1 = THRES[3];
        THRES_B2 = THRES[4];
        THRES_B3 = THRES[5];

        state = StateType.WAIT;
        prevState = StateType.WAIT;
        type = InputType.TYPE_X;

    }
    public void update (float newReading) {
        if(sampleCount > 30){
            reset();
        }
        //Switch Statement, Update previous reading
        //Update stored values
        previousReading = currentReading;
        currentReading = newReading;
        //Calculate change in acceleration

        deltaAccel = currentReading - previousReading;
        prevState = state;
        switch(state){
            case RISE_A:
                if(deltaAccel <= 0 ){
                    if(previousReading > THRES_A2){
                        state = StateType.FALL_A;
                        break;
                    }
                    else{
                        state = StateType.DETERMINED;
                        type = InputType.TYPE_X;
                    }
                }

                break;
            case FALL_B:
                if(deltaAccel >=0){
                    if(previousReading < THRES_B2){
                        state = StateType.RISE_B;
                        break;
                    }
                    else{
                        state = StateType.DETERMINED;
                        type = InputType.TYPE_X;
                    }
                }
                break;
            case WAIT:
                if (deltaAccel > THRES_A1){
                    state = StateType.RISE_A;
                }
                else if (deltaAccel < THRES_B1){
                    state = StateType.FALL_B;
                }

                break;
            case FALL_A:
                if(deltaAccel >= 0 ){
                    if(previousReading < THRES_A3){
                        state = StateType.DETERMINED;
                        type = InputType.TYPE_A;
                        break;
                    }
                    else{
                        state = StateType.DETERMINED;
                        type = InputType.TYPE_X;
                    }
                }
                break;
            case RISE_B:
                if(deltaAccel <= 0 ){
                    if(previousReading > THRES_B3){
                        state = StateType.DETERMINED;
                        type = InputType.TYPE_B;
                        break;
                    }
                    else{
                        state = StateType.DETERMINED;
                        type = InputType.TYPE_X;
                    }
                }
                break;
            case DETERMINED:
                break;
        }
        //Increment sampleCount
        if (state != StateType.WAIT) {
            sampleCount++;
        }
    }

    public InputType getType(){
        return type;
    }

    public void reset(){
        sampleCount = 0;
        previousReading = 0;
        deltaAccel = 0;
        currentReading = 0;
        type = InputType.TYPE_X;
        state = StateType.WAIT;
    }

    public boolean isDetermined() {
        return prevState != StateType.DETERMINED && state == StateType.DETERMINED;
    }

}

