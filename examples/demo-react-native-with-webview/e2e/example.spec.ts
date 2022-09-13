import {web} from "detox";

describe('Example', () => {
  before(async () => {
    await device.launchApp();
  });

  beforeEach(async () => {
    await device.reloadReactNative();
  });

  it('should fill a text field', async () => {
    let inputElement = web.element(by.web.id('standard-basic'));
    let buttonElement = web.element(by.web.xpath('//button[@data-ga-event-action="click-back-to-top"]'))

    await expect(inputElement).toExist()

    await inputElement.scrollToView()
    await inputElement.tap()
    await inputElement.typeText("Some Content", false);

    await buttonElement.tap();

    await expect(inputElement).toHaveText("Some Content")
  });

  it('should fill a react text field', async () => {
    let inputElement = web.element(by.web.id('standard-basic'));
    let buttonElement = web.element(by.web.xpath('//button[@data-ga-event-action="click-back-to-top"]'))

    await expect(inputElement).toExist()

    await inputElement.scrollToView()
    await inputElement.runScriptWithArgs('function setNativeValue(element, value) {\n' +
      '    let lastValue = element.value;\n' +
      '    element.value = value;\n' +
      '    let event = new Event("input", { target: element, bubbles: true });\n' +
      '    // React 15\n' +
      '    event.simulated = true;\n' +
      '    // React 16\n' +
      '    let tracker = element._valueTracker;\n' +
      '    if (tracker) {\n' +
      '        tracker.setValue(lastValue);\n' +
      '    }\n' +
      '    element.dispatchEvent(event);\n' +
      '}', ["Some Content 2"]);

    await buttonElement.tap();

    await expect(inputElement).toHaveText("Some Content 2")
  });
});
