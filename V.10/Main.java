public class Main {
    public static void main(String[] args) {
        //System.out.println("Args received: " + args.length + (args.length > 0 ? " → " + args[0] : " (none)"));
        String scene = args.length > 0 ? args[0] : "empire";
        switch (scene) {
            case "empire" -> RenderEmpire.renderEmpire(args);
            case "skywalker" -> RenderSkywalker.renderSkywalker(args);
            default -> RenderForties.renderForties(args);
        }
    }
}