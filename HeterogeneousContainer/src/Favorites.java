import java.util.HashMap;
import java.util.Map;

class Favorites {

  private Map<Class<?>, Object> favorites = new HashMap<>();

  <T> void putFavorite(Class<T> type, T instance) {
    if (type == null) {
      throw new NullPointerException("Type is null");
    }
    favorites.put(type, instance);
  }

  <T> T getFavorite(Class<T> type) {
    return type.cast(favorites.get(type));
  }
}
