import "./core/trustedTypePolicies";
import app from "./app";

const prepareInit = async () => {
  return Promise.resolve();
};

prepareInit().then(() => {
  app.init();
});
