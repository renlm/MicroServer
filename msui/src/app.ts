import _ from "lodash";
export class MsuiApp {
  async init() {
    console.log(_.join(["Hello", "msui"], " "));
  }
}

export default new MsuiApp();
