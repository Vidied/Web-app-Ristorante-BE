package davidepan.capstone.exceptions;

public class NotFoundException extends RuntimeException{
    public NotFoundException(String message){
        super(message);
    }

    public NotFoundException(Long id){
        super("Elemento con ID" + id + " non trovato");
    }
}
