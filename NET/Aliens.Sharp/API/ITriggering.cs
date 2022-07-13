using System;

namespace Example.API
{
    public interface ITriggering : IDisposable
    {
        public delegate bool PCallBack(int hWnd, string lParam);

        bool EnumWindows(PCallBack callback, int count);

        public event ThresholdHandler ThresholdReached;

        void StartPub(int count);

        public class ThresholdEventArgs : EventArgs
        {
            public int Threshold { get; set; }
            public DateTime TimeReached { get; set; }
        }

        public delegate void ThresholdHandler(object sender, ThresholdEventArgs e);
    }
}
