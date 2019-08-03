package net.minecraft.util;

public class ActionResult<T> {
   private final ActionResultType type;
   private final T result;

   public ActionResult(ActionResultType typeIn, T resultIn) {
      this.type = typeIn;
      this.result = resultIn;
   }

   public ActionResultType getType() {
      return this.type;
   }

   public T getResult() {
      return this.result;
   }

   //Just a generic helper function to make typecasing easier...
   public static <T> ActionResult<T> newResult(ActionResultType result, T value) {
      return new ActionResult<T>(result, value);
   }
}