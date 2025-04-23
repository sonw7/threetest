export function processData(rawData) {
    const data = rawData.trim().split(/\s+/).map(Number);
    const numGroups = data[0];
    const result = [];
  
    let index = 1;
  
    for (let i = 0; i < numGroups; i++) {
      const numPoints = data[index++];
      const numTriangles = data[index++];
  
      const vertices = [];
      const depths = [];

      for (let j = 0; j < numPoints; j++) {
        const x = data[index++];
        const y = data[index++];
        const z = data[index++];
        vertices.push(x, y, z);
        depths.push(z);
      }
  
      const indices = [];
      for (let j = 0; j < numTriangles; j++) {
        const a = data[index++];
        const b = data[index++];
        const c = data[index++];
        indices.push(a, b, c);
      }

      result.push({ vertices, indices,depths , gridSize:20,
        size:5});
    }
  
    return result;
  }