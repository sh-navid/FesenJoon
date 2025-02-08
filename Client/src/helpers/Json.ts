export class Json {
  static clone(obj: any): any | null {
    return JSON.parse(JSON.stringify(obj));
  }
}
