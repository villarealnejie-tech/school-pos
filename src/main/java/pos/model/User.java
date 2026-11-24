package pos.model;

public record User(int id, String username, String fullName, String role) {

    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }
}
