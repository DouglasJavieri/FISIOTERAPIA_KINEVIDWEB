
const buildFullDateMomentFormat = (separator: string = '/') => `DD${separator}MM${separator}YYYY HH:mm:ss`;
const buildDateMomentFormat = (separator: string = '/') => `DD${separator}MM${separator}YYYY`;

const buildFullDatePipeFormat = (separator: string = '/') => `dd${separator}MM${separator}yyyy HH:mm:ss`;
const buildDatePipeFormat = (separator: string = '/') => `dd${separator}MM${separator}yyyy`;

export const DateUtil = {
  buildFullDateMomentFormat,
  buildDateMomentFormat,
  buildFullDatePipeFormat,
  buildDatePipeFormat,
};
