using System;

using Xamarin.Forms;

namespace Foollow
{
    public class Homepage : ContentPage
    {
        public Homepage()
        {
            Content = new StackLayout
            {
                Children = {
                    new Label { Text = "Hello ContentPage" }
                }
            };
        }
    }
}

